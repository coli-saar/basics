/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.up.ling.tree;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

/**
 * A Swing component for drawing a tree.
 *
 * @author koller
 */
public class TreePanel<E> extends JPanel {

    private static final int XGAP = 10;
    private static final int YLEVEL = 50;
    private static final int CHARACTER_HEIGHT = 12;
    private static final int PADX = 10;
    private static final int PADY = 10;
    private Tree<E> tree;
    private Tree<LabelAtPosition> layoutTree;
    private Graphics currentGraphics;
    private TooltipSource<E> tooltipSource;
    private Map<String, String> nodeToTooltip;

    /*
     public static void main(String[] args) {
     Tree<String> tree = TreeParser.parse("S3(NP-SBJ2(NNP1(Dan),NNP1(Morgan)),VP3(VBD1(told),NP1(PRP1(himself)),SBAR1(S2(NP-SBJ1(PRP1(he)),VP2(MD1(would),VP2(VB1(forget),NP2(NNP1(Ann),NNP1(Turner))))))),SEP-PER1(PERIOD))");
     tree.draw();
     }
     */
    /**
     * Opens a new JFrame that displays this tree.
     *
     * @param <E>
     * @param tree
     * @return
     */
    public static <E> JFrame draw(Tree<E> tree) {
        JFrame f = new TreeFrame("Tree: " + tree.toString());
        TreePanel<E> panel = new TreePanel<E>(tree);

        Container contentPane = f.getContentPane();
        contentPane.add(panel);

        f.pack();
        f.setVisible(true);

        return f;
    }

    public TreePanel(Tree<E> tree) {
        this.tree = tree;
        layoutTree = null;
        nodeToTooltip = null;
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

    private int getLabelWidth(String label) {
        int ret = currentGraphics.getFontMetrics().stringWidth(label);
        return ret;
    }

    private Tree<LabelAtPosition> layout(Tree<E> tree) {
        Tree<LabelAtPosition> ret = tree.dfs(new TreeVisitor<E, Void, Tree<LabelAtPosition>>() {
            @Override
            public Tree<LabelAtPosition> combine(Tree<E> node, List<Tree<LabelAtPosition>> childrenValues) {
                int widthOfChildren = 0;
                int bottom = 0;

                for (Tree<LabelAtPosition> child : childrenValues) {
                    shiftRight(child, widthOfChildren);
                    shiftDown(child, YLEVEL);

                    widthOfChildren += child.getLabel().width() + XGAP;
                    bottom = Math.max(bottom, child.getLabel().height() + YLEVEL);
                }

                // determine dimensions of rectangle
                if (widthOfChildren > 0) {
                    widthOfChildren -= XGAP; // remove extra XGAP

                }

                int right = Math.max(widthOfChildren, getLabelWidth(node.getLabel().toString()));
                bottom = Math.max(bottom, CHARACTER_HEIGHT);

                // determine where the label should go
                int textCenterX = 0;
                int labelWidth = getLabelWidth(node.getLabel().toString());

                if (childrenValues.isEmpty()) {
                    // no children => put it in middle of box
                    textCenterX = right / 2;
                } else if (labelWidth > widthOfChildren) {
                    // label wider than children => nudge children to the right
                    int extraOffset = (labelWidth - widthOfChildren) / 2;

                    for (Tree<LabelAtPosition> child : childrenValues) {
                        shiftRight(child, extraOffset);
                    }

                    textCenterX = right / 2;
                } else {
                    // otherwise => center label with respect to children's labels
                    int leftOfLeftmostChildLabel = (int) childrenValues.get(0).getLabel().getTextRect().getMinX();
                    int rightOfRightmostChildLabel = (int) childrenValues.get(childrenValues.size() - 1).getLabel().getTextRect().getMaxX();
                    textCenterX = (leftOfLeftmostChildLabel + rightOfRightmostChildLabel) / 2;
                }

                LabelAtPosition label = new LabelAtPosition(0, 0, right, bottom, textCenterX, node.getLabel().toString(), node);
                return Tree.create(label, childrenValues);
            }
        });

        // add some whitespace around the actual tree
//        shiftRight(ret, PADX);
//        shiftDown(ret, PADY);
//        ret.getLabel().right += PADX;
//        ret.getLabel().bottom += PADY;
        return ret;
    }

    private void drawShortenedLine(double x1, double y1, double x2, double y2, int ydiff, float xshorten, Graphics graphics) {
        double diffx = x2 - x1;
        double diffy = y2 - y1;

        graphics.drawLine((int) (x1 + xshorten * diffx), (int) (y1 + ydiff), (int) (x2 - xshorten * diffx), (int) (y2 - ydiff));
    }

    public static interface TooltipSource<E> {

        public String makeTooltipLabel(Tree<E> tree);
    }

    public void setTooltipSource(TooltipSource<E> src) {
        this.tooltipSource = src;
        ToolTipManager.sharedInstance().registerComponent(this);
        nodeToTooltip = null;
    }

    private void ensureComputedTooltips() {
        if (nodeToTooltip == null) {
            if (tooltipSource != null) {
                nodeToTooltip = new HashMap<String, String>();
                
                layoutTree.dfs(new TreeVisitor<LabelAtPosition, Void, Void>() {
                    @Override
                    public Void combine(Tree<LabelAtPosition> node, List<Void> childrenValues) {
                        String tooltip = tooltipSource.makeTooltipLabel(node.getLabel().originalSubtree);
                        nodeToTooltip.put(node.getLabel().originalSubtree.toString(), tooltip);
                        return null;
                    }
                });
            }
        }
    }

    @Override
    public String getToolTipText(final MouseEvent e) {
        if (layoutTree == null) {
            return "";
        } else if (tooltipSource == null) {
            return "";
        } else if (!containsWithPadding(layoutTree.getLabel().getBoundingRect(), e.getPoint(), 20, 20)) {
            // optimization: if mouse is outside the bounding box of the entire tree, then do nothing
            return "";
        } else {
//            ensureComputedTooltips();
            
            String ret = layoutTree.dfs(new TreeVisitor<LabelAtPosition, Void, String>() {
                @Override
                public String combine(Tree<LabelAtPosition> node, List<String> childrenValues) {
                    // check whether mouse location was within a child => return tooltip from there
                    for (String c : childrenValues) {
                        if (c != null) {
                            return c;
                        }
                    }

                    Rectangle textRect = node.getLabel().getTextRect();
                    if (containsWithPadding(textRect, e.getPoint(), 0, 20)) {
                        return tooltipSource.makeTooltipLabel(node.getLabel().originalSubtree);
//                        return nodeToTooltip.get(node.getLabel().originalSubtree.toString());
                    } else {
                        return null;
                    }
                }
            });

            if (ret == null) {
                return "";
            } else {
                return ret;
            }
        }
    }

    private static boolean containsWithPadding(Rectangle rect, Point point, int minpad, int maxpad) {
        return rect.getMinX() - minpad <= point.x
                && rect.getMaxX() + maxpad >= point.x
                && rect.getMinY() - minpad <= point.y
                && rect.getMaxY() + maxpad >= point.y;
    }

    @Override
    public void paintComponent(final Graphics grphcs) {
        final Graphics2D graphics = (Graphics2D) grphcs;
        currentGraphics = graphics;
        boolean firstUse = false;

        if (layoutTree == null) {
            layoutTree = layout(tree);
            firstUse = true;
        }

        graphics.setColor(Color.white);
        graphics.fillRect(0, 0, layoutTree.getLabel().width() + 2 * PADX, layoutTree.getLabel().height() + 2 * PADY);
        graphics.setColor(Color.black);

        graphics.translate(PADX, PADY);

        layoutTree.dfs(new TreeVisitor<LabelAtPosition, Void, Void>() {
            @Override
            public Void visit(Tree<LabelAtPosition> node, Void data) {
                LabelAtPosition l = node.getLabel();
                Rectangle textRect = l.getTextRect();

//                graphics.setColor(Color.yellow);
//                graphics.fillRect(textRect.x, textRect.y, textRect.width, textRect.height);
//                graphics.setColor(Color.black);
//                graphics.drawRect(l.left, l.top, l.width(), l.height());
                graphics.drawString(l.label, (int) textRect.getMinX(), (int) textRect.getMaxY());

                for (Tree<LabelAtPosition> childT : node.getChildren()) {
                    LabelAtPosition child = childT.getLabel();
                    drawShortenedLine(l.getTextRect().getCenterX(), l.getTextRect().getCenterY(), child.getTextRect().getCenterX(), child.getTextRect().getCenterY(), CHARACTER_HEIGHT, 0.1f, graphics);
                }

                return null;
            }
        });

        if (firstUse) {
            revalidate();
            ((JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this)).pack();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if (layoutTree == null) {
            return new Dimension(100, 100);
        } else {
            return new Dimension(layoutTree.getLabel().width() + 2 * PADX, layoutTree.getLabel().height() + 2 * PADY);
        }
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    private class LabelAtPosition {

        public int top, left, bottom, right;
        public int textCenterX;
        public String label;
        public Tree<E> originalSubtree;

        public LabelAtPosition(int left, int top, int right, int bottom, int textCenterX, String label, Tree<E> originalSubtree) {
            this.top = top;
            this.left = left;
            this.bottom = bottom;
            this.right = right;
            this.textCenterX = textCenterX;
            this.label = label;
            this.originalSubtree = originalSubtree;
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

        public Rectangle getTextRect() {
            int textwidth = getLabelWidth(label);
            return new Rectangle(left + textCenterX - textwidth / 2, top, textwidth, CHARACTER_HEIGHT);
        }

        public Rectangle getBoundingRect() {
            return new Rectangle(left, top, width(), height());
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
            return "[top=" + top + ", left=" + left + ", bottom=" + bottom + ", right=" + right + ", tcx=" + textCenterX + ", label=" + label + ']';
        }
    }
}
