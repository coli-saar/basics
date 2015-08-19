/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.up.ling.gui.datadialog;

import java.lang.annotation.Annotation;

/**
 *
 * @author koller
 */
public class ConcreteDataField implements DataField {
    private String label;
    private Class elementClass;
    private String[] values;
    private Class<? extends ValuesProvider> valuesProvider;

    public ConcreteDataField(String label) {
        this.label = label;
    }

    public void setElementClass(Class elementClass) {
        this.elementClass = elementClass;
    }

    public void setValues(String[] values) {
        this.values = values;
    }

    public void setValuesProvider(Class<? extends ValuesProvider> valuesProvider) {
        this.valuesProvider = valuesProvider;
    }
    
    

    @Override
    public String label() {
        return label;
    }

    @Override
    public Class elementClass() {
        if( elementClass == null ) {
            return Object.class;
        } else {
            return elementClass;
        }
    }

    @Override
    public String[] values() {
        if( values != null ) {
            return values;
        } else {
            return new String[] {};
        }
    }

    @Override
    public Class<? extends ValuesProvider> valuesProvider() {
        if( valuesProvider != null ) {
            return valuesProvider;
        } else {
            return ValuesProvider.class;
        }
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return DataField.class;
    }
}
