/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.up.ling.gui.datadialog;

import java.awt.GridLayout;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 *
 * @author koller
 */
public class DataPanel extends JPanel {

    private final List<Element> elements;
    private final Object o;

    public DataPanel(String title, Object o) {
        setBorder(new TitledBorder(title));
        setLayout(new GridLayout(0, 2));

        elements = new ArrayList<Element>();
        this.o = o;
        
        Class c = o.getClass();

        for (Field f : c.getDeclaredFields()) {
            DataField anno = f.getAnnotation(DataField.class);
            if (anno != null) {
                addField(f);
            }
        }
    }

    private <E> void setField(Field f, E value) {
        try {
            if (o != null) {
                f.set(o, value);
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(DataPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(DataPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addField(Field f) {
        DataField anno = f.getAnnotation(DataField.class);
        Element e = null;
        
        if( Element.class.isAssignableFrom(anno.elementClass()) ) {
            // if element type was specified explicitly, construct an instance of it
            try {
                Constructor<Element> con = anno.elementClass().getConstructor(String.class, DataField.class);
                e = con.newInstance(f.getName(), anno);
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(DataPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
        
        // default types per field class        
        else if (String.class.isAssignableFrom(f.getType())) {
            e = new StringAsTextfieldElement(f.getName());
        }

        if (e != null) {
            e.setAction(x -> setField(f, x));
            elements.add(e);

            add(new JLabel(anno.label()));
            add(e.getComponent());
        }
    }

    public void updateFieldsFromValues() {
        for (Element e : elements) {
            e.runAction();
        }
    }

    private static class TestClass {

        @DataField(label = "lalala")
        String lala;

        @DataField(label = "zazaza", elementClass = ListAsComboBoxElement.class, values = {"mein", "alter"})
        String foo;
        
        @DataField(label = "from provider", elementClass = ListAsComboBoxElement.class, valuesProvider = TestProvider.class)
        String fromProv;

        @Override
        public String toString() {
            return "TestClass{" + "lala=" + lala + ", foo=" + foo + ", fromProv=" + fromProv + '}';
        }
        
        

    }
    
    static class TestProvider implements ValuesProvider {
        @Override
        public Object[] get() {
            return new Object[] { "a", "b", "c" };
        }
    }

    public static void main(String[] args) {
        JFrame x = new JFrame("hallo");
        x.setLayout(new GridLayout(0, 1));

        TestClass tc = new TestClass();
        DataPanel p = new DataPanel("group", tc);
        x.add(p);

        JButton b = new JButton("Ok");
        b.addActionListener(ae -> {
            p.updateFieldsFromValues();
            System.err.println(tc);
            x.setVisible(false);
            System.exit(0);
        });
        x.add(b);

//        DataPanel p = new DataPanel("group");
//        p.addField("lalalala", List.class, Arrays.asList("foo", "baa"));
//        p.addField("zazazaza", List.class, Arrays.asList("mein", "alter"));
        x.pack();
        x.setVisible(true);
    }

}
