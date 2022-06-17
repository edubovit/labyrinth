package net.edubovit.labyrinth.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ReflectionUtils {

    public static void setInt(String fieldName, Object instance, int value) throws NoSuchFieldException, IllegalAccessException {
        var field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.setInt(instance, value);
    }

    public static void setObject(String fieldName, Object instance, Object value) throws NoSuchFieldException, IllegalAccessException {
        var field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instance, value);
    }

}
