/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.up.ling.gui.datadialog.entries;

/**
 *
 * @author koller
 */
public interface DataPanelEntry<E> {

    public DataField getDataField();

    public E getValue();
    
    public void setValue(E o);

    public String getName();

    public Class getType();

}
