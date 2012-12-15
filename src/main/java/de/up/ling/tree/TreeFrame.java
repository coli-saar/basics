/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.up.ling.tree;

import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

/**
 *
 * @author koller
 */
// A TreeFrame is supposed to be closable by pressing Cmd-W.
// For some reason that I don't understand, this doesn't quite work.
class TreeFrame extends JFrame {
    public TreeFrame(String title) throws HeadlessException {
        super(title);

        getRootPane().getActionMap().put("close-window", new CloseAction(this));
        getRootPane().getInputMap().put(KeyStroke.getKeyStroke("control W"), "close-window");
        getRootPane().getInputMap().put(KeyStroke.getKeyStroke("meta W"), "close-window");
    }

    private static class CloseAction extends AbstractAction {
        private Window window;

        public CloseAction(Window window) {
            this.window = window;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (window == null) {
                return;
            }

            window.setVisible(false);
            window.dispose();
        }
    }
}
