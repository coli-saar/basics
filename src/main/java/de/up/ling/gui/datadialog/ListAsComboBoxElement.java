/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.up.ling.gui.datadialog;

import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JComponent;

/**
 *
 * @author koller
 */
public class ListAsComboBoxElement extends Element<String> {
    private JComboBox<Object> box;
    
    public ListAsComboBoxElement(String label, Object[] values) {
        super(label);
        box = new JComboBox<Object>(values);
    }
    
    public JComponent getComponent() {
        return box;
    }

    public String getValue() {
        return box.getSelectedItem().toString();
    }    
}
