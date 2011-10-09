/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.up.ling.shell;

import de.saar.basic.StringTools;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author koller
 */
public class Shell {
    private Map<String, Object> variables = new HashMap<String, Object>();
    private boolean quiet = false;
    private Object main;

    public void run(Object main) throws IOException {
        this.main = main;
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            if (!quiet) {
                System.out.print("> ");
            }
            String line = console.readLine();

            if (line == null) {
                System.exit(0);
            } else {
                try {
                    Expression expr = ShellParser.parse(new StringReader(line));
                    Object val = evaluate(expr);

                    if (!quiet) {
                        if (val != null) {
                            System.out.println(val);
                        }
                    }
                } catch (ParseException ex) {
                    System.out.println("Syntax error: " + ex.getMessage());
                } catch (Exception ex) {
                    reportException(ex);
                }
            }
        }
    }

    private Object evaluate(Expression expr) throws ParseException, FileNotFoundException, IOException {
        switch (expr.type) {
            case VARIABLE:
                return variables.get(expr.getString(0));
            case ASSIGN:
                variables.put(expr.getString(0), evaluate(expr.getExpression(1)));
                return null;
            case MAIN:
                return main;
            case READER:
                if (expr.getArgument(0) instanceof String) {
                    return new StringReader(expr.getString(0));
                } else if (expr.getArgument(0) instanceof File) {
                    return new FileReader((File) expr.getArgument(0));
                } else {
                    System.err.println("Error: Reader with content type " + expr.getArgument(0).getClass());
                    return null;
                }
            case CALL:
                Object functor = evaluate((Expression) expr.getArgument(0));
                String methodName = expr.getString(1);
                List args = evaluateList((List) expr.getArgument(2));

                Class[] argClasses = new Class[args.size()];
                Object[] argArray = new Object[args.size()];
                int argIndex = 0;
                for (Object arg : args) {
                    argClasses[argIndex] = (arg == null) ? Object.class : arg.getClass();
                    argArray[argIndex] = arg;
                    argIndex++;
                }

                try {
                    Method method = null;

                    for (Method m : functor.getClass().getMethods()) {
                        if (m.isAnnotationPresent(CallableFromShell.class) && getMethodName(m).equals(methodName)) {
                            boolean allArgumentsCompatible = (m.getParameterTypes().length == argClasses.length);

                            if (allArgumentsCompatible) {
                                for (int i = 0; i < m.getParameterTypes().length; i++) {
                                    if (!m.getParameterTypes()[i].isAssignableFrom(argClasses[i])) {
                                        allArgumentsCompatible = false;
                                    }
                                }
                            }

                            if (allArgumentsCompatible) {
                                method = m;
                            }
                        }
                    }

                    if (method == null) {
                        System.out.println("Class " + functor.getClass() + " has no method " + methodName + "(" + StringTools.join(argClasses, ",") + ").");
                        return null;
                    } else {
                        Object result = method.invoke(functor, argArray);
                        
                        if( (result instanceof Collection) && ! ann(method).joinList().equals(CallableFromShell.DO_NOT_JOIN) ) {
                            result = StringTools.join((Collection) result, ann(method).joinList());
                        }
                        
                        return result;
                    }
                } catch (Exception ex) {
                    reportException(ex);
                    return null;
                }

            case MAP:
                Map map = (Map) expr.getArgument(0);
                Map ret = new HashMap();

                for (Object key : map.keySet()) {
                    ret.put(key, evaluate((Expression) map.get(key)));
                }

                return ret;

            default:
                return null;
        }
    }
    
    private static CallableFromShell ann(Method m) {
        return m.getAnnotation(CallableFromShell.class);
    }

    private static void reportException(Exception ex) {
        System.out.println("An error occurred: " + ex.getClass() + "\n" + ex.getMessage());
        ex.printStackTrace();
    }

    private static String getMethodName(Method m) {
        String nameFromAnnotation = m.getAnnotation(CallableFromShell.class).name();

        if ("".equals(nameFromAnnotation)) {
            return m.getName();
        } else {
            return nameFromAnnotation;
        }
    }

    private List evaluateList(List<Object> list) throws ParseException, FileNotFoundException, IOException {
        List ret = new ArrayList();

        for (Object o : list) {
            if (o instanceof Expression) {
                ret.add(evaluate((Expression) o));
            } else {
                ret.add(o);
            }
        }

        return ret;
    }

    /////////////////////////////////////////
    @CallableFromShell
    public void quit() {
        System.exit(0);
    }

    @CallableFromShell
    public void println(Object val) {
        if (val != null) {
            System.out.println(val);
        }
    }

    public static void main(String args[]) throws IOException {
        Shell shell = new Shell();
        shell.run(shell);
    }
}
