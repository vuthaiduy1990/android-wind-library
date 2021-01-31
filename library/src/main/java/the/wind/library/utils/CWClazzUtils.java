package the.wind.library.utils;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;

public final class CWClazzUtils {

    private static final Map<String, Class<?>> primitiveMapper = new HashMap<>();
    private static final Map<Class<?>, Class<?>> primitiveWrapper = new HashMap<>();

    static {
        // Map primitive classname with class type
        primitiveMapper.put(boolean.class.getName(), boolean.class);
        primitiveMapper.put(char.class.getName(), char.class);
        primitiveMapper.put(byte.class.getName(), byte.class);
        primitiveMapper.put(short.class.getName(), short.class);
        primitiveMapper.put(int.class.getName(), int.class);
        primitiveMapper.put(long.class.getName(), long.class);
        primitiveMapper.put(float.class.getName(), float.class);
        primitiveMapper.put(double.class.getName(), double.class);

        // map primitive class with it's wrapper class
        primitiveWrapper.put(boolean.class, Boolean.class);
        primitiveWrapper.put(char.class, Character.class);
        primitiveWrapper.put(byte.class, Byte.class);
        primitiveWrapper.put(short.class, Short.class);
        primitiveWrapper.put(int.class, Integer.class);
        primitiveWrapper.put(long.class, Long.class);
        primitiveWrapper.put(float.class, Float.class);
        primitiveWrapper.put(double.class, Double.class);
    }

    /**
     * Convert field name in camel case.
     * <pre>
     *     mWindColor -> windColor.
     *     WindColor -> windColor
     *     windColor -> windColor
     *     mMax -> max
     *     max -> max
     *     x -> x
     *     ID -> ID
     * </pre>
     *
     * @param field class field
     * @return name in camel case
     */
    public static String toCamelCase(Field field) {
        String name = field.getName();

        // name is single character
        if (name.length() == 1) return name;

        // all is uppercase -> constant value -> keep
        // ex, ID -> ID
        if (CWStringUtils.isUpperCase(name)) return name;

        // remove 'm' character. eg, mWindColor -> windColor
        if (name.startsWith("m") && Character.isUpperCase(name.charAt(1))) {
            name = name.substring(1);
        }

        // change first word to lowercase. eg, WindColor -> windColor
        name = name.substring(0, 1).toLowerCase() + name.substring(1);

        return name;
    }

    /**
     * Get getter method of specified field.
     * <pre>
     *     mWindColor -> getWindColor,
     *     windColor -> getWindColor,
     *     boolean draggable -> isDraggable;
     * </pre>
     *
     * @param field class field
     * @return class method or null
     */
    @Nullable
    public static Method getterMethodOf(Field field) {
        Class<?> type = field.getType();
        String name = toCamelCase(field);
        String prefix = "get";

        // remove "is" if the field's type is boolean
        if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            prefix = "is";
            if (name.toLowerCase().startsWith("is")) {
                name = name.substring(2);
            }
        }

        // windColor -> WindColor
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        try {
            // add prefix then return the get method
            return field.getDeclaringClass().getMethod(prefix + name);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get setter method of specified field
     * <pre>
     *     mWindColor -> setWindColor,
     *     windColor -> setWindColor,
     *     boolean draggable -> setDraggable;
     * </pre>
     *
     * @param field class field
     * @return class method or null
     */
    @Nullable
    public static Method setterMethodOf(Field field) {
        Class<?> type = field.getType();
        String name = toCamelCase(field);
        String prefix = "set";

        // remove is if boolean
        if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            if (name.toLowerCase().startsWith("is")) {
                name = name.substring(2);
            }
        }

        // windColor -> WindColor
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        // add prefix then return the set method
        try {
            return field.getDeclaringClass().getMethod(prefix + name, type);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Wrapper of Class.forName.
     * This function will handle the case of class is primitive type
     * <pre>
     *     "float" -> float.class
     *     "int" -> int.class
     * </pre>
     *
     * @param className class name which used in import
     * @return class by given string
     *
     * @see Class#forName(String)
     */
    @Nullable
    public static Class<?> forName(String className) {
        try {
            Class<?> clazz = primitiveMapper.get(className);
            return clazz != null ? clazz : Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get type of generic field.
     * For example:
     * <pre>
     *     List<String> colors = new LinkedList();
     *     list.getClass() -> java.util.LinkedList
     *     getGenericFieldType(list) -> java.lang.String
     * </pre>
     *
     * @param field generic field. ex, List<String>
     * @return type, ex String from List<String>
     */
    @Nullable
    public static Class<?> getGenericFieldType(Field field) {
        Type genericFieldType = field.getGenericType();
        if (genericFieldType instanceof ParameterizedType) {
            ParameterizedType aType = (ParameterizedType) genericFieldType;
            Type[] fieldArgTypes = aType.getActualTypeArguments();
            if (fieldArgTypes.length > 0) {
                return (Class<?>) fieldArgTypes[0];
            }
        }
        return null;
    }

    /**
     * Check if class is number type or not
     *
     * @param clazz class
     * @return true if number
     */
    public static boolean isNumberType(Class<?> clazz) {
        return clazz.equals(Double.class) || clazz.equals(double.class)
                || clazz.equals(Float.class) || clazz.equals(float.class)
                || clazz.equals(Integer.class) || clazz.equals(int.class)
                || clazz.equals(Long.class) || clazz.equals(long.class)
                || clazz.equals(Short.class) || clazz.equals(short.class)
                || clazz.equals(Number.class);
    }

    /**
     * Check if class is primitive type (string, number, boolean) or not
     * <pre>
     *     isPrimitive(float.class) -> true
     *     isPrimitive(Float.class) -> true
     * </pre>
     *
     * @param clazz class
     * @return true if class is primitive type
     */
    public static boolean isPrimitive(Class<?> clazz) {
        return clazz.equals(Boolean.class) || clazz.equals(boolean.class)
                || clazz.equals(String.class) || clazz.equals(char.class)
                || clazz.equals(Character.class) || isNumberType(clazz)
                || clazz.equals(Byte.class) || clazz.equals(byte.class);
    }

    /**
     * Return wrapper class of specified primitive type
     * Ex, int -> Integer.
     * char -> String
     *
     * @param clazz primitive type
     * @return wrapper class of primitive type.
     */
    public static Class<?> wrapperOf(Class<?> clazz) {
        Class<?> result = primitiveWrapper.get(clazz);
        return result != null ? result : clazz;
    }

}
