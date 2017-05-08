/*
 * @(#)StringTools.java created 13.02.2006
 * 
 * Copyright (c) 2006 Alexander Koller
 *  
 */
package de.saar.basic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * A collection of various utility functions for strings. 
 * 
 * @author Alexander Koller
 *
 */
public class StringTools {
    /**
     * Joins a list of strings into a larger string. This is similar
     * to Perl's <code>join()</code> function. 
     * 
     * @param strings a list of strings
     * @param separator a string that is inserted between any two members of the list
     * @return the joined string
     */
    public static <E> String join(Iterable<E> strings, String separator) {
        boolean first = true;
        StringBuffer sb = new StringBuffer();

        for (Object s : strings) {
            if (first) {
                first = false;
            } else {
                sb.append(separator);
            }

            sb.append(s.toString());
        }

        return sb.toString();

    }

    public static <E> String join(E[] strings, String separator) {
        boolean first = true;
        StringBuffer sb = new StringBuffer();

        for (Object s : strings) {
            if (first) {
                first = false;
            } else {
                sb.append(separator);
            }

            sb.append(s.toString());
        }

        return sb.toString();
    }

    public static String removeUmlauts(String stringWithUmlauts) {
        StringBuilder buf = new StringBuilder();

        for (int i = 0; i < stringWithUmlauts.length(); i++) {
            switch (stringWithUmlauts.charAt(i)) {
                case '\u00E4':
                    buf.append("ae");
                    break;
                case '\u00F6':
                    buf.append("oe");
                    break;
                case '\u00FC':
                    buf.append("ue");
                    break;
                case '\u00C4':
                    buf.append("Ae");
                    break;
                case '\u00D6':
                    buf.append("Oe");
                    break;
                case '\u00DC':
                    buf.append("Ue");
                    break;
                case '\u00DF':
                    buf.append("ss");
                    break;
                default:
                    buf.append(stringWithUmlauts.charAt(i));
            }
        }

        return buf.toString();
    }

    public static String slurp(Reader reader) throws IOException {
        String newLine = System.getProperty("line.separator");
        BufferedReader br = new BufferedReader(reader);
        boolean first = true;

        StringBuilder sb = new StringBuilder();
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            if( first ) {
                first = false;
            } else {
                sb.append(newLine);
            }
            
            sb.append(line);
        }
        
        reader.close();
        return sb.toString();
    }
}
