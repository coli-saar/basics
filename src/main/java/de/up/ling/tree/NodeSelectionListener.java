/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.up.ling.tree;

import java.awt.Color;

/**
 *
 * @author koller
 */
public interface NodeSelectionListener<E> {
    public void nodeSelected(Tree<E> node, boolean isSelected, Color markupColor);
}
