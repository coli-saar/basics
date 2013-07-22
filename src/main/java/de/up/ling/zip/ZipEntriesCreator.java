/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.up.ling.zip;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author koller
 */
public class ZipEntriesCreator {
    private ZipOutputStream zipOutputStream;
    private int gensym;

    public ZipEntriesCreator(OutputStream ostream) {
        zipOutputStream = new ZipOutputStream(ostream);
        gensym = 1;
    }

    public void add(String entryName, Object obj) throws IOException {
        ZipEntry ze = new ZipEntry(entryName);
        zipOutputStream.putNextEntry(ze);
        new ObjectOutputStream(zipOutputStream).writeObject(obj);
    }
    
    public void add(Object obj) throws IOException {
        add("entry-" + (gensym++), obj);
    }

    public void close() throws IOException {
        zipOutputStream.close();
    }
}
