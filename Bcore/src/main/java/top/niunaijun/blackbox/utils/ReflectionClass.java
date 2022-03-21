package top.niunaijun.blackbox.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by BlackBox on 2022/3/21.
 */
public class ReflectionClass {
    @SuppressWarnings("rawtypes")
    public static void print(String name) {
        try {
            // print class name and super class name (if != object)
            Class cl = Class.forName(name);
            Class supercl = cl.getSuperclass();
            Class[] interfaces = cl.getInterfaces();
            String modifiers = Modifier.toString(cl.getModifiers());
            if (modifiers.length() > 0) {
                System.out.print(modifiers);
            }

            // is class or interface
            if (!cl.isInterface()) {
                System.out.print(" class ");
            }

            printType(cl);

            if (supercl != null && supercl != Object.class) {
                System.out.print(" extends ");
                printType(supercl);
            }

            if (interfaces.length > 0) {
                System.out.print(" implements ");

                for (int j = 0; j < interfaces.length; j++) {
                    if (j > 0) {
                        System.out.print(", ");
                    }
                    printType(interfaces[j]);
                }
            }

            System.out.print(" {\n");

            printConstructors(cl);
            System.out.println();
            printMethods(cl);
            System.out.println();
            printFields(cl);
            System.out.println("}");

            printExtendsChain(cl);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints all constructors of a class
     *
     * @param cl a class
     */
    @SuppressWarnings("rawtypes")
    private static void printConstructors(Class cl) {
        Constructor[] constructors = cl.getDeclaredConstructors();

        for (Constructor c : constructors) {
            String name = c.getName();
            System.out.print("    ");
            // print modifiers.
            String modifiers = Modifier.toString(c.getModifiers());
            if (modifiers.length() > 0) {
                System.out.print(modifiers + " ");
            }

            // print name and parameter types.
            System.out.print(name.substring(name.lastIndexOf(".") + 1) + " (");
            Class[] paramTypes = c.getParameterTypes();
            for (int j = 0; j < paramTypes.length; j++) {
                if (j > 0) {
                    System.out.print(", ");
                }
                printType(paramTypes[j]);
            }
            System.out.print(")");

            // print exceptions
            Class[] exceptions = c.getExceptionTypes();
            if (exceptions.length > 0) {
                System.out.print(" throws ");
                for (int j = 0; j < exceptions.length; j++) {
                    if (j > 0) {
                        System.out.print(", ");
                    }
                    printType(exceptions[j]);
                }
            }

            System.out.println(";");
        }
    }

    /**
     * Prints all methods of a class
     *
     * @param cl a class
     */
    @SuppressWarnings("rawtypes")
    private static void printMethods(Class cl) {
        Method[] methods = cl.getDeclaredMethods();
        for (Method m : methods) {
            Class retType = m.getReturnType();
            String name = m.getName();

            System.out.print("    ");
            // print modifiers, return type and method name
            String modifiers = Modifier.toString(m.getModifiers());
            if (modifiers.length() > 0) {
                System.out.print(modifiers + " ");
            }
            printType(retType);
            System.out.print(" " + name + "(");

            // print parameter types
            Class[] paramTypes = m.getParameterTypes();
            for (int j = 0; j < paramTypes.length; j++) {
                if (j > 0) {
                    System.out.print(", ");
                }
                printType(paramTypes[j]);
            }

            System.out.print(")");

            // print exceptions
            Class[] exceptions = m.getExceptionTypes();
            if (exceptions.length > 0) {
                System.out.print(" throws ");
                for (int j = 0; j < exceptions.length; j++) {
                    if (j > 0) {
                        System.out.print(", ");
                    }
                    printType(exceptions[j]);
                }
            }

            System.out.println(";");
        }
    }

    /**
     * Prints all fields of a class
     *
     * @param cl a class
     */
    @SuppressWarnings("rawtypes")
    private static void printFields(Class cl) {
        Field[] fields = cl.getDeclaredFields();

        for (Field f : fields) {
            Class type = f.getType();
            String name = f.getName();
            System.out.print("    ");
            String modifiers = Modifier.toString(f.getModifiers());
            if (modifiers.length() > 0) {
                System.out.print(modifiers + " ");
            }
            printType(type);
            System.out.println(" " + name + ";");
        }
    }

    /**
     * Prints a type.
     *
     * @param type a type.
     */
    @SuppressWarnings("rawtypes")
    private static void printType(Class type) {
        String name = type.getName();
        if (name.startsWith("[")) {
            if (name.startsWith("[C")) {
                System.out.print("char[]");
            } else if (name.startsWith("[B")) {
                System.out.print("byte[]");
            } else if (name.startsWith("[S")) {
                System.out.print("short[]");
            } else if (name.startsWith("[I")) {
                System.out.print("int[]");
            } else if (name.startsWith("[J")) {
                System.out.print("long[]");
            } else if (name.startsWith("[F")) {
                System.out.print("float[]");
            } else if (name.startsWith("[D")) {
                System.out.print("double[]");
            } else if (name.startsWith("[Z")) {
                System.out.print("boolean[]");
            } else if (name.startsWith("[L")) {
                System.out.print(name.substring(name.lastIndexOf(".") + 1, name.length() - 1)
                        + "[]");
            }
        } else {
            System.out.print(name.substring(name.lastIndexOf(".") + 1));
        }
    }

    /**
     * Prints the extends chain for a class
     *
     * @param cl a class
     */
    @SuppressWarnings("rawtypes")
    public static void printExtendsChain(Class cl) {
        System.out.print("\n\nExtends chain:\n    ");
        printType(cl);
        Class superclass = cl.getSuperclass();
        while (superclass != Object.class) {
            System.out.print(" --> ");
            printType(superclass);
            superclass = superclass.getSuperclass();
        }
        System.out.print(" --> ");
        printType(superclass);
    }
}
