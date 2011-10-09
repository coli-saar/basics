/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.up.ling.shell;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author koller
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CallableFromShell {
    public static final String DO_NOT_JOIN = "___DNJ___";
    
    String name() default "";
    String joinList() default DO_NOT_JOIN;
}
