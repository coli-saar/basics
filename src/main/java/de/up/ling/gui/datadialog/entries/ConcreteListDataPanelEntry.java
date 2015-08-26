/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.up.ling.gui.datadialog.entries;

import de.up.ling.gui.datadialog.ValuesProvider;
import de.up.ling.gui.datadialog.elements.ListAsComboBoxIndexElement;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.Function;

/**
 *
 * @author koller
 */
public class ConcreteListDataPanelEntry<E> implements DataPanelEntry<Integer> {

    private String[] dropdownEntries;
    private E[] values;
    private String name;
//        private String label;
    private DataField df;
    private int value;

    /**
     *
     * @param name
     * @param label
     * @param values
     * @param valueToString - pass null to just use E#toString
     */
    public ConcreteListDataPanelEntry(String name, String label, List<E> values, Function<E, String> valueToString) {
        this.values = (E[]) values.toArray();

        if (valueToString == null) {
            valueToString = (e -> e.toString());
        }

        dropdownEntries = new String[this.values.length];
        for (int i = 0; i < dropdownEntries.length; i++) {
            dropdownEntries[i] = valueToString.apply(this.values[i]);
        }

        this.name = name;
//            this.label = label;
        value = -1;

        this.df = new DataField() {
            @Override
            public String label() {
                return label;
            }

            @Override
            public Class elementClass() {
                return ListAsComboBoxIndexElement.class;
            }

            @Override
            public String[] values() {
                return dropdownEntries;
            }

            @Override
            public Class<? extends ValuesProvider> valuesProvider() {
                return null;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return DataField.class;
            }
        };
    }

    @Override
    public DataField getDataField() {
        return df;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class getType() {
        return Integer.class;
    }

    @Override
    public void setValue(Integer t) {
        value = t;
    }
    
    public E getSelectedElement() {
        if( value < 0 ) {
            return null;
        } else {
            return values[value];
        }
    }

    @Override
    public String toString() {
        return "{CLDPE " + name + ":" + getSelectedElement() + "}";
    }

}
