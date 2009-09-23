/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.saar.basic.swing;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

/**
 *
 * @author koller
 */
public class Draw {
    private static final double alpha = Math.PI / 8;

    /**
     * Draws an arrow. Used by arcs.
     */
    public static Point arrow(int x1, int y1, int x2, int y2, int size, Graphics g) {
        double dx;
        double dy;
        double d;

        double hook = 0.9;

        if (size > 6) {
            size = size - 2;
        }

        double mysin = Math.sin(alpha);
        double mycos = Math.cos(alpha);


        // draw arrow head
        Polygon shape = new Polygon();

        dx = x2 - x1;
        dy = y2 - y1;
        d = Math.sqrt((double) dx * dx + (double) dy * dy);

        dx = dx / d * size * 1.5;
        dy = dy / d * size * 1.5;


        shape.addPoint(x2, y2);
        shape.addPoint(x2 - (int) ((dx * mycos) - (dy * mysin)), y2 - (int) ((dx * mysin) + (dy * mycos)));
        shape.addPoint(x2 - (int) ((dx * hook)), y2 - (int) ((dy * hook)));
        shape.addPoint(x2 - (int) ((dy * mysin) + (dx * mycos)), y2 - (int) ((dy * mycos) - (dx * mysin)));

        g.fillPolygon(shape);

        // draw line
        g.drawLine(x1, y1, x2 - (int) ((dx * hook)), y2 - (int) ((dy * hook)));

        return new Point(x2 - (int) ((dx * hook)), y2 - (int) ((dy * hook)));
    }

    /*
    private static class Test extends JPanel {
        public Test() {
            super();

            setPreferredSize(new Dimension(500, 500));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D gg = (Graphics2D) g;
            gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            super.paintComponent(g);

            Draw.arrow(10, 10, 100, 100, 5, g);

            gg.setColor(Color.red);
            gg.setStroke(new BasicStroke(4));
            Draw.arrow(10, 100, 300, 50, 12, gg);
        }
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Draw test");

        f.add(new Test());
        f.pack();
        f.setVisible(true);
    }
     * */
}
