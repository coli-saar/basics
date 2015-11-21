package de.up.ling.tree

import org.junit.*
import java.util.*
import java.io.*
import static org.junit.Assert.*
import java.util.regex.Pattern
import com.google.common.base.Predicate
import com.google.common.base.Function

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
    
    @Test
    public void testSubstitutePredicate() {
        Tree t = p("f(x1,g(b))");        
        Predicate<Tree> pred = new TestPredicate();        
        assertEquals("f(h(c,d),g(b))", t.substitute(pred, p("h(c,d)")).toString());
    }
    
    @Test
    public void testSubstituteFunction() {
        Tree t = p("f(x1,g(x2))");
        Function<Tree> fct = new TestFunction();
        assertEquals("f(h(c,d),g(k(l)))", t.substitute(fct).toString());
    }
    
    @Test
    public void testLispNoQuote() {
        Tree t = p("foo(',', '``')");
        String s = t.toLispStringNoQuotes();
        assertEquals("(foo , ``)", s);
    }
    
    
    
    public static Tree c(Object label, List children) {
        return Tree.create(label, children);
    }
    
    
    
    public static Tree<String> p(String s) {
        return TreeParser.parse(s);
    }
}

class TestPredicate implements Predicate {
    public boolean apply(Object t) {
        return ((Tree) t).getLabel().equals("x1");
    }
}
    
class TestFunction implements Function {
    public Object apply(Object t) {
        String label = ((Tree) t).getLabel();
        
        if( label.equals("x1")) {
            return TreeTest.p("h(c,d)");
        } else if( label.equals("x2")) {
            return TreeTest.p("k(l)");
        } else {
            return null;
        }
    }
}