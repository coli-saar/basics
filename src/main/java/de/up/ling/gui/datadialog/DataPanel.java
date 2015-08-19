/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.up.ling.gui.datadialog;

import java.awt.GridLayout;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 *
 * @author koller
 */
class DataPanel extends JPanel {
    private final List<Element> elements;

    public DataPanel(String title, List<DataPanelEntry> entries) {
        setBorder(new TitledBorder(title));
        setLayout(new GridLayout(0, 2));

        elements = new ArrayList<Element>();
        entries.forEach(this::addEntry);
    }

    private void addEntry(DataPanelEntry f) {
        DataField anno = f.getDataField();
        Element e = null;

        if (Element.class.isAssignableFrom(anno.elementClass())) {
            // if element type was specified explicitly, construct an instance of it
            try {
                Constructor<Element> con = anno.elementClass().getConstructor(String.class, DataField.class);
                e = con.newInstance(f.getName(), anno);
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(DataPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } // default types per field class        
        else if (String.class.isAssignableFrom(f.getType())) {
            e = new StringAsTextfieldElement(f.getName());
        }

        if (e != null) {
            e.setAction(f);
            elements.add(e);

            add(new JLabel(anno.label()));
            add(e.getComponent());
        }
    }

    public void updateFieldsFromValues() {
        for (Element e : elements) {
            e.runAction();
        }
    }
}
