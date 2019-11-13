import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class Deserializer {
    private static Map<Long, Object> map;

    private  void initialize(){
        map = new HashMap<>();
    }
    public void deserialize(Document doc) throws Exception{
        initialize();
        //File inputFile = new File("input.txt");
        //SAXBuilder saxBuilder = new SAXBuilder();
        //Document doc1 = saxBuilder.build(inputFile);
        Element root = doc.getRootElement();
        List<Element> children = root.getChildren();
        // Initialize hash map
        loadObjectsIntoHashMap(children);
        // Set fields
        setFields(children);
        Inspector inspector = new Inspector();
        inspector.inspect(map.get(0L), true);
    }


    private void loadObjectsIntoHashMap(List<Element> elements) throws Exception{
        for (Element element : elements){
            Class objectClass = Class.forName(element.getAttributeValue("name"));
            long key = Long.valueOf(element.getAttributeValue("id"));
            Object object;
            if (Collection.class.isAssignableFrom(objectClass)){
                // Collection type
                object = new ArrayList<>();
            }
            else if (objectClass.isArray()){
                // Handle the case where it is an array
                object = Array.newInstance(objectClass.getComponentType(),
                        Integer.valueOf(element.getAttributeValue("length")));
            } else {
                // Non-array object
                object = objectClass.getDeclaredConstructor().newInstance();
            }
            map.put(key, object);
        }
    }

    private void setFields(List<Element> elements) throws Exception{
        for (Element element : elements){
            long key = Long.valueOf(element.getAttributeValue("id"));
            Object storedObject = map.get(key);
            if (Collection.class.isAssignableFrom(storedObject.getClass())){
                // Collection type
                setCollectionValueForObject(storedObject, element.getChildren());
            }
            else if (!storedObject.getClass().isArray()){
                // Handle the case where it is non array-object
                setFieldValueForObject(storedObject, element.getChildren());
            } else {
                // Array object
                setArrayValueForObject(storedObject, element.getChildren());
            }
        }
    }

    private void setCollectionValueForObject(Object obj, List<Element> values) throws Exception{
        // Obj is a collection
        Collection colObj = (Collection) obj;
        for (int i = 0; i < values.size(); i++){
            Element field = values.get(i);
            if (field.getAttributeValue("name").equals("elementData")){
                for (Element refElement : field.getChildren()){
                    Collections.addAll(colObj, map.get(Long.valueOf(refElement.getText())));
                }
            }
        }
    }

    private void setFieldValueForObject(Object obj, List<Element> elementFields) throws Exception{
        for (Element elementField : elementFields){
            // Ignore the null, static and final fields
            if (elementField.getChildren().size() == 0) continue;
            String fieldName = elementField.getAttributeValue("name");
            String declaringClass = elementField.getAttributeValue("declaringclass");
            // Get the field
            Field field = obj.getClass().getDeclaredField(fieldName);
            // Ignore the static, final fields
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) continue;
            field.setAccessible(true);
            // Find the appropriate wrapper
            Class fieldClass = Utils.parseType(declaringClass);
            if (fieldClass.isPrimitive()){
                field.set(obj, Double.valueOf(elementField.getChild("value").getText()));
            } else {
                long keyRef = Long.valueOf(elementField.getChild("reference").getText());
                field.set(obj, map.get(keyRef));
            }
        }
    }

    private void setArrayValueForObject(Object obj, List<Element> values){
        if (obj.getClass().getComponentType().isPrimitive()){
            for (int i = 0; i < values.size(); i++)
                Array.set(obj, i, Double.valueOf(values.get(i).getText()));
        } else {
            for (int i = 0; i < values.size(); i++){
                Array.set(obj, i, map.get(Long.valueOf(values.get(i).getText())));
            }
        }
    }
}
