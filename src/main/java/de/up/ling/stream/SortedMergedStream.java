/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.up.ling.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A stream consisting of the values of several other streams.
 * The values are presented in ascending order, according to
 * the ordering specified in the given comparator. This class
 * assumes that the underlying streams that are being merged
 * also present their values in ascending order.
 * 
 * @author koller
 */
public class SortedMergedStream<E> implements Stream<E> {
    private List<Stream<E>> streams;
    private Comparator<Stream<E>> streamComparator;
    private boolean finished = false;
    /** set to true by sort(), to false by all operations modifying the stream */
    private boolean sorted = false;

    public SortedMergedStream(Comparator<E> comparator) {
        streams = new ArrayList<Stream<E>>();
        streamComparator = new StreamComparator(comparator);
    }

    public void addStream(Stream<E> stream) {
        streams.add(stream);
        sorted = false;
    }

    public E peek() {
        if (streams.isEmpty()) {
            return null;
        } else {
            sort();
            return streams.get(0).peek();
        }
    }

    public E pop() {
        if (streams.isEmpty()) {
            return null;
        } else {
            sort();
            // after the pop operation below, this stream will not be sorted anymore.
            sorted = false;
            return streams.get(0).pop();
        }
    }

    @Override
    public boolean isFinished() {
        if (finished) {
            return true;
        } else {
            finished = true;

            for (Stream<E> s : streams) {
                if (!s.isFinished()) {
                    finished = false;
                }
            }

            return finished;
        }
    }

    private void sort() {
        if (sorted) {
            return;
        }
        Collections.sort(streams, streamComparator);
        sorted = true;
    }
    
    private class StreamComparator implements Comparator<Stream<E>> {
        private final Comparator<E> underlyingComparator;

        public StreamComparator(Comparator<E> underlyingComparator) {
            this.underlyingComparator = underlyingComparator;
        }
        
        public int compare(Stream<E> s1, Stream<E> s2) {
            E val1 = s1.peek();
            E val2 = s2.peek();
            
            return underlyingComparator.compare(val1, val2);
        }        
    }
}
