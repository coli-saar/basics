/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.up.ling.gui.datadialog.elements;

import static de.up.ling.gui.datadialog.elements.Element.getValuesFromAnnotation;
import de.up.ling.gui.datadialog.entries.DataField;
import javax.swing.JComboBox;
import javax.swing.JComponent;

/**
 *
 * @author koller
 */
public class ListAsComboBoxIndexElement extends Element<Integer> {
    
    private JComboBox<Object> box;

    public ListAsComboBoxIndexElement(String label, DataField df, Integer originalIndex) {
        super(label);
        box = new JComboBox<Object>(getValuesFromAnnotation(df));
        
        if( originalIndex >= 0 ) {
            box.setSelectedItem(originalIndex);
        }
    }

    @Override
    public JComponent getComponent() {
        return box;
    }

    /**
     *
     * @return
     */
    public Integer getValue() {
        return box.getSelectedIndex();
    }
}
