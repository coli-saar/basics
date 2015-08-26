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
public abstract class AbstractDataField implements DataField {

    @Override
    public abstract String label();

    @Override
    public Class elementClass() {
        return Object.class;
    }

    @Override
    public String[] values() {
        return null;
    }

    @Override
    public Class<? extends ValuesProvider> valuesProvider() {
        return null;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return AbstractDataField.class;
    }

}
