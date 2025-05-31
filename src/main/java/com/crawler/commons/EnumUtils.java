package com.crawler.commons;


public class EnumUtils {

    public static <E extends Enum<E>> E getOrDefault(
            final Class<E> enumClass, final String enumName, final E defaultEnum
    ) {
        if (enumName == null) {
            return defaultEnum;
        }
        try {
            return Enum.valueOf(enumClass, enumName);
        } catch (final IllegalArgumentException ex) {
            return defaultEnum;
        }
    }

}
