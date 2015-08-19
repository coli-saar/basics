/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.up.ling.gui.datadialog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author koller
 */
class ReflectionEntry implements DataPanelEntry {
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
    public void accept(Object value) {
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

    public static List<DataPanelEntry> forObject(Object o) {
        List<DataPanelEntry> ret = new ArrayList<>();
        for (Field f : o.getClass().getDeclaredFields()) {
            if (f.getAnnotation(DataField.class) != null) {
                ret.add(new ReflectionEntry(f, o));
            }
        }
        return ret;
    }
    
}
