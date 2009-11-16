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
        assert StringTools.removeUmlauts("\u00DCbrige Uml\u00E4ute m\u00FC\u00F6\u00DFen \u00C4 asjd\u00D6.").equals("Uebrige Umlaeute mueoessen Ae asjdOe.");
    }
}

