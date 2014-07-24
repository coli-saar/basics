/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.up.ling.tree;

import java.util.List;

/**
 *
 * @author koller
 */
public interface TreeBottomUpVisitor<E,Up> {
   public Up combine(Tree<E> node, List<Up> childrenValues);
}
