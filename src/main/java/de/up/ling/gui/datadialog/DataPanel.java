/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.up.ling.gui.datadialog;

import java.awt.GridLayout;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
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

    private List<Element> elements;
    private Object o;

    public DataPanel(String title) {
        setBorder(new TitledBorder(title));
        setLayout(new GridLayout(0, 2));

        elements = new ArrayList<Element>();
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

        if (List.class.isAssignableFrom(anno.type())) {
            e = new ListAsComboBoxElement(f.getName(), anno.values());
            e.setAction(x -> setField(f, x));
            elements.add(e);
        }

        if (e != null) {
            add(new JLabel(anno.label()));
            add(e.getComponent());
        }
    }

    public void updateFieldsFromValues() {
        for (Element e : elements) {
            e.runAction();
        }
    }

    public static DataPanel forObject(String title, Object o) {
        DataPanel ret = new DataPanel(title);
        ret.o = o;

        Class c = o.getClass();

        for (Field f : c.getDeclaredFields()) {
            DataField anno = f.getAnnotation(DataField.class);
            if (anno != null) {
                ret.addField(f);
            }
        }

        return ret;
    }

    private static class TestClass {

        @DataField(label = "lalala", type = List.class, values = {"foo", "bar"})
        String lala;

        @DataField(label = "zazaza", type = List.class, values = {"mein", "alter"})
        String foo;

        @Override
        public String toString() {
            return "TestClass{" + "lala=" + lala + ", foo=" + foo + '}';
        }
        
        
    }

    public static void main(String[] args) {
        JFrame x = new JFrame("hallo");
        x.setLayout(new GridLayout(0,1));

        TestClass tc = new TestClass();
        DataPanel p = DataPanel.forObject("group", tc);
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
