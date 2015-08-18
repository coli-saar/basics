/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.up.ling.gui.datadialog;

import java.util.function.Consumer;
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
        if( action != null ) {
            action.accept(getValue());
        }
    }
    
    public abstract JComponent getComponent();
    public abstract F getValue();
}
