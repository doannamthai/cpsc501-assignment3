public class Utils {
    public static String getIndentation(int depth){
        StringBuilder indent = new StringBuilder();
        while (depth-- > 0)
            indent.append("\t");
        return indent.toString();
    }

    public static boolean isPrimitiveOrWrapper(Class<?> type) {
        return (type.isPrimitive() && type != void.class) ||
                type == Double.class || type == Float.class || type == Long.class ||
                type == Integer.class || type == Short.class || type == Character.class ||
                type == Byte.class || type == Boolean.class || type == String.class;
    }
}
