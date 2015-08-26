/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.up.ling.gui.datadialog;

import de.up.ling.gui.datadialog.entries.DataField;
import de.up.ling.gui.datadialog.elements.ListAsComboBoxElement;
import de.up.ling.gui.datadialog.entries.ReflectionEntry;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author koller
 */
public class DataDialog extends JDialog {
    private List<DataPanel> panels;
    
    private DataDialog(Frame owner, String title, List<DataPanelContainer> entries, Consumer<List<DataPanelContainer>> callback) {
        super(owner);
        setTitle(title);
        
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        
        panels = new ArrayList<>();
        for( int i = 0; i < entries.size(); i++ ) {
            DataPanel p = new DataPanel(entries.get(i));
            panels.add(p);
            getContentPane().add(p);
        }
        
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.LINE_AXIS));
        getContentPane().add(buttonsPanel);
        
        JButton ok = new JButton("Ok");
        ok.addActionListener(ae -> {
           for( DataPanel p : panels ) {
               p.updateFieldsFromValues();
           }
           DataDialog.this.setVisible(false);
           
           // run callback in new thread
           new Thread(() -> { callback.accept(entries); }).start();
        });
        buttonsPanel.add(ok);
        buttonsPanel.add(Box.createRigidArea(new Dimension(10, 1)));
        
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(ae -> { 
           DataDialog.this.setVisible(false);
        });
        buttonsPanel.add(cancel);
        buttonsPanel.add(Box.createHorizontalGlue());
        
        pack();
    }
    
    /**
     * Only runs callback if user clicked "Ok". If user clicked "Cancel",
     * nothing happens.
     * 
     * @param owner
     * @param title
     * @param groupNames
     * @param entries
     * @param callback 
     */
    public static void withValues(Frame owner, String title, List<DataPanelContainer> entries, Consumer<List<DataPanelContainer>> callback) {
        SwingUtilities.invokeLater(() -> {
           DataDialog dd = new DataDialog(owner, title, entries, callback);
           dd.setVisible(true);
        });
    }
    
    

    public  static class TestClass {

        @DataField(label = "lalala")
        public String lala;

        @DataField(label = "from provider", elementClass = ListAsComboBoxElement.class, valuesProvider = TestProvider.class)
        public String fromProv;

        @Override
        public String toString() {
            return "TestClass{" + "lala=" + lala + ", fromProv=" + fromProv + '}';
        }

        public static class TestProvider implements ValuesProvider {

            @Override
            public Object[] get() {
                return new Object[]{"a", "b", "c"};
            }
        }
    }

    public static class OtherClass {

        @DataField(label = "zazaza", elementClass = ListAsComboBoxElement.class, values = {"mein", "alter"})
        public String foo;
        
        @DataField(label = "check me")
        public Boolean boolField;

        @Override
        public String toString() {
            return "OtherClass{" + "foo=" + foo + ", boolField=" + boolField + '}';
        }

        
        

    }

    public static void main(String[] args) {
        TestClass tc = new TestClass();
        OtherClass oc = new OtherClass();

        DataDialog.withValues(null, "dialog", 
                Arrays.asList(ReflectionEntry.forObject("group", tc), ReflectionEntry.forObject("other group", oc)),
                
                (dpc) -> {
                    // access original objects if you still have them ...
                    System.err.println(tc);
                    
                    // ... or access by name in the DataPanelContainer
                    System.err.println(dpc.get(0));
                    
                    System.err.println(oc);
                });
        
//        DataPanelEntry x = new DataPanelEntry() {
//            public String val = "val";
//            
//            @Override
//            public DataField getDataField() {
//                ConcreteDataField df = new ConcreteDataField("fooo label");
//                df.setElementClass(String.class);
//                return df;
//            }
//
//            @Override
//            public Object getValue() {
//                return val;
//            }
//
//            @Override
//            public String getName() {
//                return "foo name";
//            }
//
//            @Override
//            public Class getType() {
//                return String.class;
//            }
//
//            @Override
//            public void accept(Object t) {
//                val = (String) t;
//            }
//        };
//        
//        DataDialog.withValues(null, "dialog2", 
//                Arrays.asList("group1"),
//                Arrays.asList(Arrays.asList(x)),
//                
//                () -> {
//                    System.err.println(x.getValue());
//                });
    }
}
