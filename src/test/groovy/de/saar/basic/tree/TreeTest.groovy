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
        Tree<String> tree = term.toTree();
        assert tree.toString().equals("f(g(a), b)") : tree.toString();
    }

    @Test
    public void testUnparseParse() {
        Term term = TermParser.parse("f(g(a),b)");
        Tree<String> tree = term.toTree();
        Term t2 = TermParser.parse(tree.toString());
        assert term.equals(t2) : t2;
   }

    @Test
    public void testTreeToTerm() {
        Term term = TermParser.parse("f(g(a),b)");
        Tree<String> tree = term.toTree();
        Term decoded = tree.toTerm();

        assert term.equals(decoded);
    }

    @Test
    public void testAddSubtree(){
        Term term1 = TermParser.parse("f(g(a),b)");
        Term term2 = TermParser.parse("p(c)");
        Tree<Term> tree1 = term1.toTree();
        Tree<Term> tree2 = term2.toTree();
        String root = tree1.getRoot();
        tree1.addSubTree(tree2,root);

        Term finl = TermParser.parse("f(g(a),b,p(c))");
        Tree<Term> treef = finl.toTree();

        assert tree1.equals(treef);
    }

    @Test
    public void testReplaceNode(){
        Term finl = TermParser.parse("f(g(a),b,p(c))");
        Tree<Term> treef = finl.toTree();
        Term newT = TermParser.parse("x");
        Tree<Term> newTf = newT.toTree();

        String node = treef.getNodeAtAddress("2");
        treef.replaceNode(node,newTf);

        Term t1 = TermParser.parse("f(g(a),b,x)");
        Tree<Term> tree1 = t1.toTree();

        assert tree1.equals(treef);

    }
}

