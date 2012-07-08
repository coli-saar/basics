/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.up.ling.tree


import org.junit.*
import java.util.*
import java.io.*
import static org.junit.Assert.*


/**
 *
 * @author koller
 */
class TreeParserTest {
    @Test
    public void testParse1() {
        Tree t = p("a")
        Tree gold = c("a", [])
        
        assertEquals(gold, t)
    }
    
    @Test
    public void testParse2() {
        Tree t = p("f(a,b)")
        Tree gold = c("f", [c("a", []), c("b", [])])
        
        assertEquals(gold, t)
    }
    
    public static Tree c(Object label, List children) {
        return Tree.create(label, children);
    }
    
    
    
    public static Tree<String> p(String s) {
        return TreeParser.parse(s);
    }
}

