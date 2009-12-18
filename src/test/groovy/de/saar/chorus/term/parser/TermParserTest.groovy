
package de.saar.chorus.term.parser;

import org.junit.*;
import de.saar.chorus.term.*;


public class TermParserTest {
	@Test
	public void testEmptyBrackets() {
		Term x = TermParser.parse("movetwosteps ( )");
		assert x instanceof Compound;
	}
}