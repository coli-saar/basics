package de.up.ling.tree

import org.junit.*
import java.util.*
import java.io.*
import static org.junit.Assert.*
import java.util.regex.Pattern

class TreeTest {
    @Test
    public void testConstruct() {
        Tree t = c("a", [])
        
        assertEquals("a", t.toString())
    }
    
    @Test
    public void testConstruct2() {
        Tree t = c("f", [c("a", []), c("b", [])])
        assertEquals("f(a,b)", t.toString())
    }
    
    @Test
    public void testClone() {
        Tree t = p("f(a,g(b))")
        Tree copy = (Tree) t.clone()
        assertEquals(t,copy)
    }
    
    @Test
    public void testDfs() {
        Tree t = p("f(a,g(b))");
        List labels = t.getAllNodes().collect { it.getLabel() };
        List gold = ["f", "a", "g", "b"];
        
        assertEquals(gold, labels)
    }
    
    @Test
    public void testToStringQuoting() {
        Tree t = p("'  f  '('?_*')")
        assertEquals("'  f  '('?_*')", t.toString())
    }

    @Test
    public void testToStringQuotingCustomized() {
        Pattern pat = Pattern.compile("[a-zA-z*+_?]([a-zA-Z0-9_*+-]*)");
        Tree t = p("'  f  '('?_*')")
        assertEquals("'  f  '(?_*)", t.toString(pat))
    }
    
    
    public static Tree c(Object label, List children) {
        return Tree.create(label, children);
    }
    
    
    
    public static Tree<String> p(String s) {
        return TreeParser.parse(s);
    }
}
