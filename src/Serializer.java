import objects.ObjectWithPrimitiveArray;
import objects.ObjectWithReference;
import objects.SimpleObject;
import objects.SupportObject;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class Serializer {
    private static final Map<Object, Long> map = new IdentityHashMap<Object, Long>();
    private static long nextId = 0;

    public static void main(String[] args) throws Exception{
        SupportObject supportObject = new ObjectWithReference();
        Serializer serializer = new Serializer();
        serializer.serialize(supportObject);
    }

    public static long getIdFor(Object o) {
        Long l = map.get(o);
        if (l == null) map.put(o, l = nextId++);
        return l;
    }

    public static void remove(Object o) {
        map.remove(o);
    }

    public void serialize(SupportObject supportObject) throws Exception{
        Element root = new Element("serialized");
        serializeObject(root, supportObject);
        Document doc = new Document(root);
        XMLOutputter xmlOutput = new XMLOutputter();

        // display ml
        xmlOutput.setFormat(Format.getPrettyFormat());
        xmlOutput.output(doc, System.out);
    }

    private Element serializeObject(Element root, Object obj) throws Exception{
        Class objectClass = obj.getClass();
        Element object = new Element("object");
        // Set name
        object.setAttribute("name", objectClass.getName());
        // Set id
        object.setAttribute("id", String.valueOf(getIdFor(obj)));
        // If this is an array type
        if (objectClass.isArray()){
            object.setAttribute("length", String.valueOf(Array.getLength(obj)));
            handleArray(root, object, obj);
        } else {
            object.addContent(getFields(root, obj, objectClass));
        }
        root.addContent(0, object);
        return object;
    }

    private void handleArray(Element main, Element root, Object obj) throws Exception{
        for (int i = 0; i < Array.getLength(obj); i++){
            Object element = Array.get(obj, i);
            if (Utils.isPrimitiveOrWrapper(element.getClass())){
                Element value = new Element("value");
                value.setText(element.toString());
                root.addContent(value);
            } else {
                Element ref = new Element("reference");
                ref.setText(String.valueOf(getIdFor(element)));
                root.addContent(ref);
                // Serialize this object
                serializeObject(main, element);
            }
        }
    }

    private List<Element> getFields(Element main, Object obj, Class objectClass) throws Exception{
        List<Element> res = new ArrayList<>();
        Field[] fields = objectClass.getDeclaredFields();
        for (Field field: fields){
            Element root = new Element("field");
            Object fieldVal = field.get(obj);
            if (fieldVal != null){
                Element value;
                // Handle the case when this field is primitive
                if (field.getType().isPrimitive()){
                    value = new Element("value");
                    value.setText(fieldVal.toString());
                } else {
                    // This is an object
                    value = new Element("reference");
                    value.setText(String.valueOf(getIdFor(fieldVal)));
                    // Serialize this object
                    serializeObject(main, fieldVal);
                }
                root.addContent(value);
            }
            root.setAttribute("name", field.getName());
            root.setAttribute("declaringclass", field.getType().getName());
            res.add(root);
        }
        return res;
    }
}
