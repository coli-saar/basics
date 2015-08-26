/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.up.ling.gui.datadialog.entries;

import de.up.ling.gui.datadialog.DataPanel;
import de.up.ling.gui.datadialog.DataPanelContainer;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author koller
 */
public class ReflectionEntry implements DataPanelEntry {
    Field f;
    Object o;

    public ReflectionEntry(Field f, Object o) {
        this.f = f;
        this.o = o;
    }

    @Override
    public DataField getDataField() {
        return f.getAnnotation(DataField.class);
    }

    @Override
    public void setValue(Object value) {
        try {
            f.set(o, value);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(DataPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Object getValue() {
        try {
            return f.get(o);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(DataPanel.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public String getName() {
        return f.getName();
    }

    @Override
    public Class getType() {
        return f.getType();
    }

    public static DataPanelContainer forObject(String name, Object o) {
        DataPanelContainer ret = new DataPanelContainer(name);
        for (Field f : o.getClass().getDeclaredFields()) {
            if (f.getAnnotation(DataField.class) != null) {
                ret.addEntry(new ReflectionEntry(f, o));
            }
        }
        
        return ret;
    }

    @Override
    public String toString() {
        try {
            return "<refl " + f.get(o) + ">";
        } catch (Exception ex) {
            return "<refl XXX>";
        } 
    }
    
    
}
