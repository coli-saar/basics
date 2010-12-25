/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.saar.basic;

/**
 *
 * @author koller
 */
public class Pair<E,F> {
    public E left;
    public F right;

    public Pair(E left, F right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pair<E, F> other = (Pair<E, F>) obj;
        if (this.left != other.left && (this.left == null || !this.left.equals(other.left))) {
            return false;
        }
        if (this.right != other.right && (this.right == null || !this.right.equals(other.right))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }


    @Override
    public String toString() {
        return left + "," + right;
    }

    
}
