/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.up.ling.tree;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author koller
 */
public class TreePanel<E> extends JPanel {
    private Tree<E> tree;
    private Tree<LabelAtPosition> layoutTree;
    private Graphics currentGraphics;

    public static <E> JFrame draw(Tree<E> tree) {
        JFrame f = new TreeFrame("Tree: " + tree.toString());
        TreePanel<E> panel = new TreePanel<E>(tree);

        f.add(panel);
        f.pack();
        f.setVisible(true);

        return f;
    }

    public TreePanel(Tree<E> tree) {
        this.tree = tree;
        layoutTree = null;
    }

    private class LabelAtPosition {
        public int top, left, bottom, right;
        public String label;

        public LabelAtPosition(int left, int top, int right, int bottom, String label) {
            this.top = top;
            this.left = left;
            this.bottom = bottom;
            this.right = right;
            this.label = label;
        }

        public int width() {
            return right - left;
        }

        public int getSwingX() {
            return left + (width() - getLabelWidth(label)) / 2;
        }

        public int getSwingY() {
            return top + CHARACTER_HEIGHT;
        }
        
        public int getTextCenterX() {
            return left + width()/2;
        }
        
        public int getTextCenterY() {
            return top + CHARACTER_HEIGHT/2;
        }

        public int height() {
            return bottom - top;
        }

        public void shiftRight(int x) {
            left += x;
            right += x;
        }

        public void shiftDown(int y) {
            top += y;
            bottom += y;
        }

        @Override
        public String toString() {
            return "[top=" + top + ", left=" + left + ", bottom=" + bottom + ", right=" + right + ", label=" + label + ']';
        }
        
        
    }

    private void shiftRight(Tree<LabelAtPosition> tree, final int x) {
        tree.dfs(new TreeVisitor<LabelAtPosition, Void, Void>() {
            @Override
            public Void visit(Tree<LabelAtPosition> node, Void data) {
                node.getLabel().shiftRight(x);
                return null;
            }
        });
    }

    private void shiftDown(Tree<LabelAtPosition> tree, final int y) {
        tree.dfs(new TreeVisitor<LabelAtPosition, Void, Void>() {
            @Override
            public Void visit(Tree<LabelAtPosition> node, Void data) {
                node.getLabel().shiftDown(y);
                return null;
            }
        });
    }
    
    
    private static final int XGAP = 10;
    private static final int YLEVEL = 50;
    private static final int CHARACTER_HEIGHT = 10;

    private int getLabelWidth(String label) {
        int ret = currentGraphics.getFontMetrics().stringWidth(label);
//        System.err.println("width of " + label + " is " + ret);
        return ret;
    }

    private <E> Tree<LabelAtPosition> layout(Tree<E> tree) {
        return tree.dfs(new TreeVisitor<E, Void, Tree<LabelAtPosition>>() {
            @Override
            public Tree<LabelAtPosition> combine(Tree<E> node, List<Tree<LabelAtPosition>> childrenValues) {
                int offset = 0;
                int bottom = 0;

                for (Tree<LabelAtPosition> child : childrenValues) {
                    shiftRight(child, offset);
                    shiftDown(child, YLEVEL);

                    offset += child.getLabel().width() + XGAP;
                    bottom = Math.max(bottom, child.getLabel().height());
                }
                
                // remove extra XGAP
                if( offset > 0 ) {
                    offset -= XGAP;
                }
                
                int right = Math.max(offset, getLabelWidth(node.getLabel().toString()));
                bottom = Math.max(bottom, CHARACTER_HEIGHT);

                LabelAtPosition label = new LabelAtPosition(0, 0, right, bottom, node.getLabel().toString());
                return Tree.create(label, childrenValues);
            }
        });
    }

    private void drawShortenedLine(int x1, int y1, int x2, int y2, Graphics graphics) {
        float m = ((float)y2-y1)/(x2-x1);
    }
    
    @Override
    public void paint(final Graphics grphcs) {
        currentGraphics = grphcs;

        if (layoutTree == null) {
            layoutTree = layout(tree);
        }



        layoutTree.dfs(new TreeVisitor<LabelAtPosition, Void, Void>() {
            @Override
            public Void visit(Tree<LabelAtPosition> node, Void data) {
                LabelAtPosition l = node.getLabel();
                grphcs.drawString(l.label, l.getSwingX(), l.getSwingY());
                
                for( Tree<LabelAtPosition> childT : node.getChildren() ) {
                    LabelAtPosition child = childT.getLabel();
                    grphcs.drawLine(l.getTextCenterX(), l.getTextCenterY(), child.getTextCenterX(), child.getTextCenterY());
                }
                
                return null;
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        if (layoutTree == null) {
            return new Dimension(100, 100);
        } else {
            return new Dimension(layoutTree.getLabel().width(), layoutTree.getLabel().height());
        }
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }
}
