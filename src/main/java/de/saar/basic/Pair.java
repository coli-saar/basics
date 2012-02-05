/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.saar.basic;

import java.io.Serializable;

/**
 * An immutable pair. While you can modify `left' and `right' directly
 * (an early sin in implementing this class), doing so invalidates
 * the hashCode. So if you plan on using Pairs as keys of HashMaps,
 * you should refrain from doing this. Also, you are encouraged to use
 * getLeft() and getRight() instead of accessing the fields directly,
 * as these may be made private at a future time.
 * 
 * @author koller
 */
public class Pair<E, F> implements Serializable {
    public E left;
    public F right;
    private int cachedHashCode;

    public Pair(E left, F right) {
        this.left = left;
        this.right = right;
        cachedHashCode = 0;
    }

    public E getLeft() {
        return left;
    }

    public F getRight() {
        return right;
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
        if (cachedHashCode == 0) {
            cachedHashCode = 7;
            cachedHashCode = 11 * cachedHashCode + (this.left != null ? this.left.hashCode() : 0);
            cachedHashCode = 11 * cachedHashCode + (this.right != null ? this.right.hashCode() : 0);
        }
        
        return cachedHashCode;
    }

    @Override
    public String toString() {
        return left + "," + right;
    }
}
