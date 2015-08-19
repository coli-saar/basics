/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.up.ling.gui.datadialog;

import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 *
 * @author koller
 */
class StringAsTextfieldElement extends Element<String> {
    private JTextField tf;

    public StringAsTextfieldElement(String label) {
        super(label);
        tf = new JTextField();
    }

    @Override
    public JComponent getComponent() {
        return tf;
    }

    @Override
    public String getValue() {
        return tf.getText();
    }
    
}
