
import java.lang.reflect.*;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.StringJoiner;

public class Inspector {

    public void inspect(Object obj, boolean recursive) {
        Class c = obj.getClass();
        try {
            inspectClass(c, obj, recursive, 0);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void inspectClass(Class c, Object obj, boolean recursive, int depth) throws IllegalAccessException {
        // Handle null objects
        // Stop here if this is Object class, primitive or primitive wrapper
        if (c == null || obj == null || c.equals(Object.class) || c.isPrimitive() || Utils.isPrimitiveOrWrapper(c))
            return;
        // Get the class name
        String className = findClassName(c, obj, recursive, depth);
        // If this an array inspect elements
        if (c.isArray()){
            Object[] array = (Object[]) obj;
            System.out.println(MessageFormat.format("{0}Type: array | Component type: {1} | Length: {2} | Content: {3}", getIndentation(depth),
                    obj.getClass().getComponentType(), Array.getLength(obj), Arrays.toString(array)));
            for (int i = 0; i < array.length; i++){
                if (array[i] != null)
                inspectClass(array[i].getClass(), array[i], recursive, depth+1);
            }
        }

        // Find super-class information if applicable
        //findSuperClassInfo(className, c, obj, recursive, depth);
        // Find interface information if applicable
        //findInterfaceInfo(className, c, obj, recursive, depth);
        // Find constructors information
        //findConstructorInfo(className, c, obj, recursive, depth);
        // Find method information
        //findMethodInfo(className, c, obj, recursive, depth);
        // Find field information
        findFieldInfo(className, c, obj, recursive, depth);
    }

    public String findClassName(Class c, Object obj, boolean recursive, int depth){
        // Get the class name
        String className = c.getSimpleName();
        System.out.format("%sClass name: %s\n", getIndentation(depth), className);
        return className;
    }


    public void findFieldInfo(String className, Class c, Object obj, boolean recursive, int depth) throws IllegalAccessException{
        Field[] fields =  c.getDeclaredFields();
        System.out.format("%s[%s] Field(s) information\n", getIndentation(depth), className);
        depth += 1;
        for (Field field : fields){
            // Necessary to be able to read a private field
            field.setAccessible(true);
            System.out.format("%sField name: %s\n", getIndentation(depth), field.getName());
            System.out.format("%sType: %s\n", getIndentation(depth), field.getType().getTypeName());
            System.out.format("%sModifiers: %s\n", getIndentation(depth), Modifier.toString(field.getModifiers()));
            System.out.format("%sCurrent value: %s\n", getIndentation(depth), getCurrentFieldValue(field, obj));
            if (recursive)
                recursiveInspect(field.get(obj), depth);
            System.out.println();

        }
    }

    public void recursiveInspect(Object obj, int depth) throws IllegalAccessException{
        // If this object is null or class is primitive then exits
        if (obj == null || obj.getClass() == null || obj.getClass().equals(Object.class)
                || obj.getClass().isPrimitive() || Utils.isPrimitiveOrWrapper(obj.getClass()))
            return;

        // If this is an array of objects then iterate over the array and inspect element
        System.out.format("%s========================= Element(s) inspection ========================= \n", getIndentation(depth));
        if (obj.getClass().isArray()){
            for (int i = 0; i < Array.getLength(obj); i++) {
                Object element = Array.get(obj, i);
                if (element != null) inspectClass(element.getClass(), element, true, depth);
            }
        }
        else inspectClass(obj.getClass(), obj, true, depth);
    }

    private String getIndentation(int depth){
        StringBuilder indent = new StringBuilder();
        while (depth-- > 0)
            indent.append("\t");
        return indent.toString();
    }

    private boolean hasSuperClass(Class c){
        return c.getSuperclass() != null;
    }

    private boolean hasInterface(Class c){
        return c.getInterfaces().length > 0;
    }


    private String arrayToString(Class[] array){
        if (array.length == 0) return "Empty";
        StringJoiner stringJoiner = new StringJoiner(", ");
        for (Class c : array) stringJoiner.add(c.getTypeName());
        return stringJoiner.toString();
    }

    private Object getCurrentFieldValue(Field f, Object obj) throws IllegalAccessException{
        Object val = f.get(obj);
        if (val == null) return null;
        if (val.getClass().isArray()){
            Object[] array = convertToArray(val);
            return MessageFormat.format("Name: array | Component type: {0} | Length: {1} | Content: {2}",
                    val.getClass().getComponentType(), array.length, Arrays.toString(array));
        } else {
            if (Utils.isPrimitiveOrWrapper(val.getClass())){
                return val;
            } else {
                return val.getClass().getName()+"@"+Integer.toHexString(System.identityHashCode(val));
            }
        }
    }

    private Object[] convertToArray(Object val){
        Object[] array;
        // Check if this is primitive type
        if (val.getClass().getComponentType().isPrimitive()) {
            array = new Object[Array.getLength(val)];
            for(int i = 0; i < array.length; ++i)
                array[i] = Array.get(val, i);
        } else array = (Object[]) val;
        return array;
    }


}