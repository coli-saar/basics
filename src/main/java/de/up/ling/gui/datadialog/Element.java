/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.up.ling.gui.datadialog;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;

/**
 *
 * @author koller
 */
public abstract class Element<F> {

    private String label;
    private Consumer<F> action;

    protected Element(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setAction(Consumer<F> action) {
        this.action = action;
    }

    public void runAction() {
        if (action != null) {
            action.accept(getValue());
        }
    }

    protected static Object[] getValuesFromAnnotation(DataField df) {
        if (df.values().length > 0) {
            return df.values();
        } else if (df.valuesProvider() != ValuesProvider.class) {
            try {
                ValuesProvider vp = df.valuesProvider().newInstance();
                Method m = df.valuesProvider().getDeclaredMethod("get");
                return (Object[]) m.invoke(vp);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException ex) {
                Logger.getLogger(Element.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        } else {
            return null;
        }
    }

    public abstract JComponent getComponent();

    public abstract F getValue();
}
