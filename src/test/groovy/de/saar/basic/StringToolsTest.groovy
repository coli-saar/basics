/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.saar.basic

import org.junit.*;

/**
 *
 * @author koller
 */
public class StringToolsTest {
    @Test
    public void testRemoveUmlauts() {
        assert StringTools.removeUmlauts("†brige UmlŠute mŸš§en € asjd….").equals("Uebrige Umlaeute mueoessen Ae asjdOe.");
    }
}

