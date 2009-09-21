/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.saar.basic.filewatcher;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author koller
 */
public class FileWatcher {
    private int interval;
    private File file;
    private List<FileChangeListener> listeners;
    private Thread watcherThread;

    public FileWatcher(File file, int interval) {
        this.interval = interval;
        this.file = file;
        listeners = new ArrayList<FileChangeListener>();
        watcherThread = new WatcherThread();
    }

    public void addFileChangeListener(FileChangeListener listener) {
        listeners.add(listener);
    }

    public void startWatching() {
        watcherThread.start();
    }

    private class WatcherThread extends Thread {
        private long lastModificationTime = System.currentTimeMillis();

        @Override
        public void run() {
            try {
                while (!interrupted()) {
                    long modificationTime = file.lastModified();

                    if (modificationTime > lastModificationTime) {
                        for (FileChangeListener listener : listeners) {
                            listener.fileChanged(file, modificationTime);
                        }

                        lastModificationTime = modificationTime;
                    }

                    Thread.sleep(1000 * interval);
                }
            } catch (InterruptedException e) {
            }
        }
    }

    /***** for testing ****/
    public static void main(String[] args) {
        FileWatcher fw = new FileWatcher(new File("foo"), 1);
        fw.addFileChangeListener(new TestListener());
        fw.startWatching();
    }

    private static class TestListener implements FileChangeListener {
        public void fileChanged(File file, long lastModificationTime) {
            System.err.println(file + " changed at " + new Date(lastModificationTime));
        }
    }
}
