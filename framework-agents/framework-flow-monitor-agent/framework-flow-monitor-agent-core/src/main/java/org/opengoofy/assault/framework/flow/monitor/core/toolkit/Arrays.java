package org.opengoofy.assault.framework.flow.monitor.core.toolkit;

import java.lang.reflect.Array;

/**
 * 数组工具类
 */
public class Arrays {
    
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }
    
    public static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }
    
    public static <T> T firstMatch(Matcher<T> matcher, T... array) {
        if (!isEmpty(array)) {
            for (final T val : array) {
                if (matcher.match(val)) {
                    return val;
                }
            }
        }
        return null;
    }
    
    public static <T> T[] addAll(final T[] array1, @SuppressWarnings("unchecked") final T... array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        final Class<?> type1 = array1.getClass().getComponentType();
        @SuppressWarnings("unchecked")
        final T[] joinedArray = (T[]) Array.newInstance(type1, array1.length + array2.length);
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        try {
            System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        } catch (final ArrayStoreException ase) {
            final Class<?> type2 = array2.getClass().getComponentType();
            if (!type1.isAssignableFrom(type2)) {
                throw new IllegalArgumentException("Cannot store " + type2.getName() + " in an array of " + type1.getName(), ase);
            }
            throw ase;
        }
        return joinedArray;
    }
    
    public static <T> T[] clone(final T[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }
}
