package top.canyie.pine.utils;

import android.os.Build;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import top.canyie.pine.PineConfig;

/**
 * @author canyie
 */
public final class ReflectionHelper {
    private static Field override;

    private ReflectionHelper() {
    }

    public static void forceAccessible(AccessibleObject member) {
        try {
            member.setAccessible(true);
            if (member.isAccessible()) return;
        } catch (SecurityException ignored) {
        }

        if (override == null) {
            override = getField(AccessibleObject.class, PineConfig.sdkLevel >= Build.VERSION_CODES.N ? "override" : "flag");
        }

        try {
            override.setBoolean(member, true);
        } catch (IllegalAccessException e) {
            throw new SecurityException("Cannot set AccessibleObject.override", e);
        }
    }

    public static Field getField(Class<?> c, String name) {
        Field field = findField(c, name);
        if (field == null) throw new IllegalArgumentException("No field " + name + " found in " + c);
        return field;
    }

    public static Field findField(Class<?> c, String name) {
        for (;c != null;c = c.getSuperclass()) {
            try {
                Field field = c.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException ignored) {
            }
        }
        return null;
    }

    public static Method getMethod(Class<?> c, String name, Class<?>... paramTypes) {
        Method method = findMethod(c, name, paramTypes);
        if (method == null)
            throw new IllegalArgumentException("No method " + name + " with params " + Arrays.toString(paramTypes) + " found in " + c);
        return method;
    }

    public static Method findMethod(Class<?> c, String name, Class<?>... paramTypes) {
        for (;c != null;c = c.getSuperclass()) {
            try {
                Method method = c.getDeclaredMethod(name, paramTypes);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException ignored) {
            }
        }
        return null;
    }

    public static <T> Constructor<T> getConstructor(Class<T> c, Class<?>... paramTypes) {
        try {
            Constructor<T> constructor = c.getDeclaredConstructor(paramTypes);
            forceAccessible(constructor);
            return constructor;
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("No constructor found with params " + Arrays.toString(paramTypes), e);
        }
    }

    public static <T> Constructor<T> findConstructor(Class<T> c, Class<?>... paramTypes) {
        try {
            Constructor<T> constructor = c.getDeclaredConstructor(paramTypes);
            forceAccessible(constructor);
            return constructor;
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }
}
