/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.saar.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author koller
 */
public class CartesianIterator<E> implements Iterator<List<E>> {
    private List<? extends Collection<E>> collections;
    private List<Iterator<E>> iterators;
    private List<E> currentValues;
    private boolean hasNext;

    public CartesianIterator(List<? extends Collection<E>> collections) {
        this.collections = collections;

        /*
        this.collections = new ArrayList<Collection<E>>();
        for( Collection<E> x : collections ) {
            List<E> xx = new ArrayList<E>();
            xx.addAll(x);
            this.collections.add(xx);
        }
         *
         */

        this.iterators = new ArrayList<Iterator<E>>();
        this.currentValues = new ArrayList<E>();
        hasNext = true;

        for (int i = 0; i < collections.size(); i++) {
            Iterator<E> it = this.collections.get(i).iterator();
            iterators.add(it);

            if (it.hasNext()) {
                currentValues.add(it.next());
            } else {
                hasNext = false;
            }
        }
    }

    public boolean hasNext() {
        return hasNext;
    }

    public List<E> next() {
        List<E> ret = new ArrayList<E>(currentValues);

        for( int i = 0; i < collections.size(); i++ ) {
            if( iterators.get(i).hasNext() ) {
                E val = iterators.get(i).next();
                currentValues.set(i, val);
                return ret;
            } else {
                Iterator<E> it = collections.get(i).iterator();
                iterators.set(i, it);
                currentValues.set(i, it.next());
            }
        }

        hasNext = false;
        return ret;
    }

    public void remove() {
        throw new UnsupportedOperationException("Not supported.");
    }
}
