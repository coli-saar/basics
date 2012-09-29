/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.up.ling.stream;

/**
 * A stream of values of type E. A stream is a possibly infinite
 * list, whose values may be computed lazily. It is related closely
 * to a Java iterator, but has a slightly different signature.
 * 
 * @author koller
 */
public interface Stream<E> {
    /**
     * Returns the next element of the stream, without removing it.
     * 
     * @return null if the stream cannot provide values at this time
     */
    public E peek();
    
    /**
     * Returns the next element of the stream and removes it from the stream.
     * 
     * @return null if the stream cannot provide values at this time
     */
    public E pop();
    
    /**
     * Checks whether the stream contains further values. It is allowed
     * for peek and pop to return null, but isFinished to return false;
     * this means that no values are available right now, but they might
     * become available at some later time.
     * 
     * @return 
     */
    public boolean isFinished();
}
