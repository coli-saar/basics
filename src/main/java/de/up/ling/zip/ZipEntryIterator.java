/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.up.ling.zip;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * @author koller
 */
public class ZipEntryIterator<E> implements Iterator<E> {
    private ZipInputStream zipInputStream;
    private ZipEntry nextEntry;

    public ZipEntryIterator(InputStream istream) throws IOException {
        zipInputStream = new ZipInputStream(istream);
        nextEntry = zipInputStream.getNextEntry();
    }

    public boolean hasNext() {
        return nextEntry != null;
    }

    public E next() {
        try {
            E ret = (E) new ObjectInputStream(zipInputStream).readObject();
            nextEntry = zipInputStream.getNextEntry();
            return ret;
        } catch (IOException ex) {
            Logger.getLogger(ZipEntryIterator.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ZipEntryIterator.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public void remove() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
