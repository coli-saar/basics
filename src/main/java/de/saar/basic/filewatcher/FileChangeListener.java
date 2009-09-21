/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.saar.basic.filewatcher;

import java.io.File;

/**
 *
 * @author koller
 */
public interface FileChangeListener {
    public void fileChanged(File file, long lastModificationTime);
}
