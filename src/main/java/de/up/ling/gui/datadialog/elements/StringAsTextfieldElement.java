/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.up.ling.gui.datadialog.elements;

import de.up.ling.gui.datadialog.entries.DataField;
import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 *
 * @author koller
 */
public class StringAsTextfieldElement extends Element<String> {
    private JTextField tf;

    public StringAsTextfieldElement(String label, DataField df, String originalValue) {
        super(label);
        tf = new JTextField();
        if( originalValue != null ) {
            tf.setText(originalValue);
        }
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
