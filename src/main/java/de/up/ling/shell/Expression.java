/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.up.ling.shell;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author koller
 */
public class Expression {
    public static enum Type {
        ASSIGN, CALL, VARIABLE, MAIN, READER, MAP, NOP
    };
    
    public static final Expression MAIN = new Expression(Type.MAIN);
    
    public Type type;
    public List<Object> arguments;

    public Expression(Type type, Object... args) {
        this.type = type;
        arguments = Arrays.asList(args);
    }
    
    public Object getArgument(int i) {
        return arguments.get(i);
    }

    public String getString(int i) {
        return (String) arguments.get(i);
    }

    public Expression getExpression(int i) {
        return (Expression) arguments.get(i);
    }

    @Override
    public String toString() {
        return type + arguments.toString();
    }
}
