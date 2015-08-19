/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.up.ling.gui.datadialog;

import java.util.function.Consumer;

/**
 *
 * @author koller
 */
public interface DataPanelEntry extends Consumer<Object> {

    public DataField getDataField();

    public Object getValue();

    public String getName();

    public Class getType();

}
