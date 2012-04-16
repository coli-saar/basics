/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.saar.basic;

import java.util.*;

/**
 *
 * @author koller
 */
public class Agenda<E> extends AbstractQueue<E> {
    private Queue<E> agenda;
    private Set<E> seen;

    public Agenda() {
        agenda = new LinkedList<E>();
        seen = new HashSet<E>();
    }

    public int size() {
        return agenda.size();
    }

    public Iterator<E> iterator() {
        return agenda.iterator();
    }

    public E poll() {
        return agenda.poll();
    }

    public E peek() {
        return agenda.peek();
    }

    public boolean offer(E e) {
        if (seen.contains(e)) {
            return false;
        } else {
            seen.add(e);
            return agenda.offer(e);
        }
    }
}
