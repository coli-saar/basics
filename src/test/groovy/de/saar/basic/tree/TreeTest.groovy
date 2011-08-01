/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.saar.basic.tree

import org.junit.*;
import de.saar.chorus.term.*;
import de.saar.chorus.term.parser.*;

/**
 *
 * @author koller
 */
class TreeTest {
    @Test
    public void testTreeConstruction() {
        Tree<String> tree = new Tree<String>();

        String root = tree.addNode("l1", null);
        String n2 = tree.addNode("l2", root);
        String n3 = tree.addNode("l3", root);
        String n4 = tree.addNode("l3", n2);

        assert tree.getAllNodes().equals(new HashSet(["n1", "n2", "n3", "n4"]));
        assert tree.getChildren("n1").equals(["n2", "n3"]);
    }

    @Test
    public void testLabels() {
        Tree<String> tree = new Tree<String>();

        String root = tree.addNode("l1", null);
        String n2 = tree.addNode("l2", root);
        String n3 = tree.addNode("l3", root);
        String n4 = tree.addNode("l3", n2);

        assert tree.getLabel(root).equals("l1");
        assert tree.getLabel(n3).equals("l3");
    }
    
    @Test
    public void testNodeAtAddress() {
        Tree<String> tree = new Tree<String>();

        String root = tree.addNode("l1", null);
        String n2 = tree.addNode("l2", root);
        String n3 = tree.addNode("l3", root);
        String n4 = tree.addNode("l3", n2);
        
        assert tree.getNodeAtAddress("00").equals(n4);
    }

    @Test
    public void testDfs() {
        Tree<String> tree = new Tree<String>();

        String root = tree.addNode("l1", null);
        String n2 = tree.addNode("l2", root);
        String n3 = tree.addNode("l3", root);
        String n4 = tree.addNode("l3", n2);

        assert tree.getNodesInDfsOrder().equals([n4, n2, n3, root]);
    }

    @Test
    public void testTermToTree() {
        Term term = TermParser.parse("f(g(a),b)");
        Tree<Term> tree = term.toTree();
        assert tree.toString().equals("f(g(a ) b )") : tree.toString();
    }

    @Test
    public void testTreeToTerm() {
        Term term = TermParser.parse("f(g(a),b)");
        Tree<Term> tree = term.toTree();
        Term decoded = tree.toTerm();

        assert term.equals(decoded);
    }
}

