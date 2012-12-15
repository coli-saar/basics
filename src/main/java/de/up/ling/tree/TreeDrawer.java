/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.up.ling.tree;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellView;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;
import org.jgraph.layout.TreeLayoutAlgorithm;
import org.jgraph.util.JGraphUtilities;

/**
 *
 * @author koller
 */
public class TreeDrawer {
    private Font nodeFont;
    private Object[] allNodes;
    private JGraph graph;
    private final Map<String, DefaultGraphCell> nodeForName = new HashMap<String, DefaultGraphCell>();

    
    public static <E> JFrame draw(Tree<E> tree) {
        JFrame f = new TreeFrame("Tree: " + tree.toString());
        JGraph g = new JGraph();
        TreeDrawer v = new TreeDrawer();
        v.draw(tree, g);

        f.add(g);
        f.pack();
        f.setVisible(true);

        v.computeLayout(g);
        f.pack();

        return f;
    }

    public TreeDrawer() {
        nodeFont = GraphConstants.DEFAULTFONT.deriveFont(Font.PLAIN, 17);
    }

    public <E> void draw(final Tree<E> tree, final JGraph graph) {
        graph.getModel().remove(JGraphUtilities.getAll(graph));

        int N = tree.getAllNodes().size();
        allNodes = new Object[N];


//        final List<DefaultGraphCell> cells = new ArrayList<DefaultGraphCell>(N);

        this.graph = graph;

        Tree<DefaultGraphCell> cells = tree.dfs(new TreeVisitor<E, Void, Tree<DefaultGraphCell>>() {
            @Override
            public Tree<DefaultGraphCell> combine(Tree<E> node, List<Tree<DefaultGraphCell>> childrenValues) {
                DefaultGraphCell cell = addNode(node.getLabel().toString(), graph);

                for (Tree<DefaultGraphCell> child : childrenValues) {
                    addEdge(cell, child.getLabel(), graph);
                }

                return Tree.create(cell, childrenValues);
            }
        });

        cells.dfs(new TreeVisitor<DefaultGraphCell, Void, Void>() {
            private int i = 0;

            @Override
            public Void visit(Tree<DefaultGraphCell> node, Void data) {
                allNodes[i++] = node.getLabel();
                return null;
            }
        });
    }

    public void computeLayout(JGraph graph) {
        fixWidths();
        JGraphUtilities.applyLayout(graph, allNodes, new TreeLayoutAlgorithm());
        graph.validate();
    }

    private void fixWidths() {
        Map<DefaultGraphCell, AttributeMap> viewMap = new HashMap<DefaultGraphCell, AttributeMap>();

        for (String nodename : nodeForName.keySet()) {
            DefaultGraphCell node = nodeForName.get(nodename);

            CellView view = graph.getGraphLayoutCache().getMapping(node, false);
            Rectangle2D rect = (Rectangle2D) view.getBounds().clone();
            Rectangle bounds = new Rectangle((int) rect.getX(), (int) rect.getY(),
                    (int) getNodeWidth(node.toString()), (int) rect.getHeight());

            AttributeMap map = new AttributeMap();
            GraphConstants.setBounds(map, (Rectangle2D) bounds.clone());

            viewMap.put(node, map);
        }

        graph.getGraphLayoutCache().edit(viewMap, null, null, null);
    }

    /**
     * Adds a new node to the graph (and the underlying model). The attributes
     * of the new node are computed automatically from the node data.
     *
     * @param data the data for the new node.
     * @return a new DefaultGraphCell object in this graph.
     */
    private DefaultGraphCell addNode(String data, JGraph graph) {
        DefaultGraphCell ret = new DefaultGraphCell(data);
        GraphModel model = graph.getModel();

        AttributeMap style = defaultNodeAttributes(data);

        Map attributes = new HashMap();
        attributes.put(ret, style);

        DefaultPort port = new DefaultPort();
        ret.add(port);

        model.insert(new Object[]{ret, port}, attributes, new ConnectionSet(), null, null);

        return ret;
    }

    /**
     * Adds a new edge to the graph (and the underlying model). The edge goes
     * from the 0-th port of the node src to the 0-th port of the node tgt. The
     * style attributes of the new edge are computed automatically from the edge
     * data.
     *
     * @param data the data for the new edge.
     * @param src the node cell at which the edge should start.
     * @param tgt the node cell at which the edge should end.
     * @return a new DefaultEdge object in this graph.
     */
    private DefaultEdge addEdge(DefaultGraphCell src, DefaultGraphCell tgt, JGraph graph) {
        DefaultEdge ret = new DefaultEdge("");

        GraphModel model = graph.getModel();
        AttributeMap style = defaultEdgeAttributes();
        Map attributes = new HashMap();
        attributes.put(ret, style);


        ConnectionSet cs = new ConnectionSet();
        cs.connect(ret, src.getChildAt(0), tgt.getChildAt(0));
        model.insert(new Object[]{ret}, attributes, cs, null, null);

        return ret;
    }

    protected AttributeMap defaultNodeAttributes(String label) {
        AttributeMap map = new AttributeMap();
        GraphConstants.setForeground(map, Color.black);

        GraphConstants.setBounds(map, map.createRect(0, 0, getNodeWidth(label), getNodeHeight(label)));

        GraphConstants.setBackground(map, Color.white);
        GraphConstants.setFont(map, nodeFont);
        GraphConstants.setOpaque(map, true);

        return map;
    }

    protected AttributeMap defaultEdgeAttributes() {
        AttributeMap solidEdge = new AttributeMap();
        GraphConstants.setLineEnd(solidEdge, GraphConstants.ARROW_NONE);
        GraphConstants.setEndSize(solidEdge, 10);
        GraphConstants.setLineWidth(solidEdge, 1.7f);
        return solidEdge;
    }

    private int getNodeHeight(String label) {
        /*
         * This has been the standard value for JDomGraph objects.
         * TODO think about something more elegant here...
         */
        return 30;
    }

    private int getNodeWidth(String label) {
        Graphics g = graph.getGraphics();

        int ret = 30; // a default node width

        if ((g != null) && !"".equals(label)) {
            g.setFont(nodeFont);
            FontMetrics metrics = g.getFontMetrics();
            double trueWidth = metrics.stringWidth(label);
            if (trueWidth > ret) {
                ret = (int) trueWidth;
            }
        }

        return ret;
    }

    
}
