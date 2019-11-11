
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

        // Find super-class information if applicable
        findSuperClassInfo(className, c, obj, recursive, depth);
        // Find interface information if applicable
        findInterfaceInfo(className, c, obj, recursive, depth);
        // Find constructors information
        findConstructorInfo(className, c, obj, recursive, depth);
        // Find method information
        findMethodInfo(className, c, obj, recursive, depth);
        // Find field information
        findFieldInfo(className, c, obj, recursive, depth);
    }

    public String findClassName(Class c, Object obj, boolean recursive, int depth){
        // Get the class name
        String className = c.getSimpleName();
        System.out.format("%sClass name: %s\n", getIndentation(depth), className);
        return className;
    }

    public void findSuperClassInfo(String className, Class c, Object obj, boolean recursive, int depth) throws IllegalAccessException{
        if (hasSuperClass(c)){
            System.out.format("%s[%s] Super-class information\n", getIndentation(depth), className);
            inspectClass(c.getSuperclass(), obj, recursive, depth+1);
        } else {
            System.out.format("%s[%s] No super-class\n", getIndentation(depth), className);
        }
    }

    public void findInterfaceInfo(String className, Class c, Object obj, boolean recursive, int depth) throws IllegalAccessException{
        if (hasInterface(c)){
            System.out.format("%s[%s] Interface(s) information", getIndentation(depth), className);
            for (int i = 0; i < c.getInterfaces().length; i++) {
                System.out.println();
                inspectClass(c.getInterfaces()[i], obj, recursive, depth + 1);
            }
        } else {
            System.out.format("%s[%s] No interface(s)\n",getIndentation(depth), className);
        }
    }

    public void findConstructorInfo(String className, Class c, Object obj, boolean recursive, int depth){
        Constructor[] constructors =  c.getDeclaredConstructors();
        System.out.format("%s[%s] Constructor(s) information\n", getIndentation(depth), className);
        depth += 1;
        for (Constructor constructor : constructors){
            constructor.setAccessible(true);
            System.out.format("%sConstructor name: %s\n", getIndentation(depth), constructor.getName());
            System.out.format("%sParameter types (%d): %s\n", getIndentation(depth), constructor.getParameterTypes().length,
                    arrayToString(constructor.getParameterTypes()));
            System.out.format("%sModifiers: %s", getIndentation(depth), Modifier.toString(constructor.getModifiers()));
            System.out.println("\n");
        }
    }

    public void findMethodInfo(String className, Class c, Object obj, boolean recursive, int depth){
        Method[] methods = c.getDeclaredMethods();
        System.out.format("%s[%s] Method(s) information\n", getIndentation(depth), className);
        depth += 1;
        for (Method method : methods){
            method.setAccessible(true);
            System.out.format("%sMethod name: %s\n", getIndentation(depth), method.getName());
            System.out.format("%sExceptions thrown (%d): %s\n", getIndentation(depth), method.getExceptionTypes().length,
                    arrayToString(method.getExceptionTypes()));
            System.out.format("%sParameter types (%d): %s\n", getIndentation(depth), method.getParameterTypes().length,
                    arrayToString(method.getParameterTypes()));
            System.out.format("%sReturn type: %s\n", getIndentation(depth), method.getReturnType().getTypeName());
            System.out.format("%sModifiers: %s", getIndentation(depth), Modifier.toString(method.getModifiers()));
            System.out.println("\n");
        }
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
        if (obj == null) return;
        // If this object is null or class is primitive then exits
        if (obj.getClass() != null && obj.getClass().isPrimitive())
            return;
        // If this is an array of objects then iterate over the array and inspect element
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