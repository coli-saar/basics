/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.up.ling.gui.datadialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author koller
 */
public class DataPanelContainer {
    private String name;
    private List<DataPanelEntry>  entries;
    private Map<String,DataPanelEntry> nameToEntry;
    
    public DataPanelContainer(String name) {
        this.name = name;
        this.entries = new ArrayList<>();
        nameToEntry = new HashMap<>();
    }

    public DataPanelContainer(String name, DataPanelEntry... entries) {
        this(name);
        
        for( DataPanelEntry e : entries ) {
            addEntry(e);
        }
    }
    
    public void addEntry(DataPanelEntry entry) {
        entries.add(entry);
        nameToEntry.put(entry.getName(), entry);
    }

    public List<DataPanelEntry> getEntries() {
        return entries;
    }
    
    public DataPanelEntry getEntry(String name) {
        return nameToEntry.get(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return nameToEntry.toString();
    }
    
    
}
