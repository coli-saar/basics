/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.up.ling.gui.datadialog;

import de.up.ling.gui.datadialog.entries.DataField;
import de.up.ling.gui.datadialog.elements.Element;
import de.up.ling.gui.datadialog.elements.BooleanAsCheckboxElement;
import de.up.ling.gui.datadialog.elements.StringAsTextfieldElement;
import de.up.ling.gui.datadialog.entries.DataPanelEntry;
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
public class DataPanel extends JPanel {
    private final List<Element> elements;

    private DataPanel(String title, List<DataPanelEntry> entries) {
        setBorder(new TitledBorder(title));
        setLayout(new GridLayout(0, 2));

        elements = new ArrayList<Element>();
        entries.forEach(this::addEntry);
    }

    DataPanel(DataPanelContainer container) {
        this(container.getName(), container.getEntries());
    }

    private void addEntry(DataPanelEntry f) {
        DataField anno = f.getDataField();
        Element e = null;

        if (Element.class.isAssignableFrom(anno.elementClass())) {
            // if element type was specified explicitly, construct an instance of it
            try {
                Constructor<Element> con = anno.elementClass().getConstructor(String.class, DataField.class, f.getType());
                e = con.newInstance(f.getName(), anno, f.getValue());
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(DataPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } // default types per field class        
        else if (String.class.isAssignableFrom(f.getType())) {
            e = new StringAsTextfieldElement(f.getName(), anno, (String) f.getValue());
        } else if( Boolean.class.isAssignableFrom(f.getType())) {
            e = new BooleanAsCheckboxElement(f.getName(), anno, (Boolean) f.getValue());
        }

        if (e != null) {
            e.setAction(f::setValue);
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
