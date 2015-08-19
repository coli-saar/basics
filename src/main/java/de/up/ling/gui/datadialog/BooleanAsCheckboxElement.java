/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.up.ling.gui.datadialog;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

/**
 *
 * @author koller
 */
public class BooleanAsCheckboxElement extends Element<Boolean> {
    private JCheckBox box;
    
    public BooleanAsCheckboxElement(String label, DataField df, Boolean originalValue) {
        super(label);
        box = new JCheckBox();
        
        if( originalValue != null ) {
            box.setSelected(originalValue);
        }
    }

    @Override
    public JComponent getComponent() {
        return box;
    }

    @Override
    public Boolean getValue() {
        return box.isSelected();
    }
    
}
