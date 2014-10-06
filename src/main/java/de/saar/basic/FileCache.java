/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.saar.basic;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author koller
 */
public class FileCache {

    private static class CachedFile {

        public File filename;
        public String contents;
        public long lastModified;

        public CachedFile(File filename) throws IOException {
            this.filename = filename;
            contents = StringTools.slurp(new FileReader(filename));
            lastModified = filename.lastModified();
        }

        public Reader getReader() {
            return new StringReader(contents);
        }

        public void updateIfNeeded() throws IOException {
            long modified = filename.lastModified();

            if (modified > lastModified) {
                contents = StringTools.slurp(new FileReader(filename));
                lastModified = modified;
            }
        }
    }

    private final Map<String, CachedFile> cachedFiles;

    public FileCache() {
        cachedFiles = new HashMap<String, CachedFile>();
    }

    public synchronized Reader getReader(String id) throws IOException {
        CachedFile cf = cachedFiles.get(id);

        if (cf == null) {
            cf = cacheNewFile(id);
            cachedFiles.put(id, cf);
        } else {
            cf.updateIfNeeded();
        }

        return cf.getReader();
    }

    private CachedFile cacheNewFile(String id) throws IOException {
        return new CachedFile(new File(id));
    }
}
