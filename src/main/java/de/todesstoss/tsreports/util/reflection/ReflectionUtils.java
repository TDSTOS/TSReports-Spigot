package de.todesstoss.tsreports.util.reflection;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class ReflectionUtils {

    public static <T> void setField(@NotNull Object object,
                                    @NotNull String fieldName,
                                    @NotNull T value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            boolean accessible = field.isAccessible();

            field.setAccessible(true);
            field.set(object, value);
            field.setAccessible(accessible);
        } catch (Exception e) {
            throw new NullPointerException(e.getMessage());
        }
    }

}
