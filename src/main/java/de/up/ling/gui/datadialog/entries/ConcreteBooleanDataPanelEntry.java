/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.up.ling.gui.datadialog.entries;

import de.up.ling.gui.datadialog.ValuesProvider;
import java.lang.annotation.Annotation;

/**
 *
 * @author koller
 */
public class ConcreteBooleanDataPanelEntry implements DataPanelEntry<Boolean> {

    private Boolean value;
    private String name;
    private String label;
    private DataField df;

    public ConcreteBooleanDataPanelEntry(String name, String label) {
        this.name = name;
        this.label = label;
        this.value = false;
        
        
        this.df = new AbstractDataField() {
            @Override
            public String label() {
                return label;
            }
        };
    }

    
    
    @Override
    public DataField getDataField() {
        return df;
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public void setValue(Boolean o) {
        value = o;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class getType() {
        return Boolean.class;
    }

}
