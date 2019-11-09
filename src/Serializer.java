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
import java.util.List;

public class Serializer {
    public static void main(String[] args) throws Exception{
        SupportObject supportObject = new ObjectWithReference();
        Serializer serializer = new Serializer();
        serializer.serialize(supportObject);
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
        object.setAttribute("name", objectClass.getName());
        if (objectClass.isArray()){
            object.setAttribute("length", Array.getLength(obj)+"");
        }
        object.addContent(getFields(obj, objectClass));
        root.addContent(object);
        return object;
    }

    private List<Element> getFields(Object obj, Class objectClass) throws Exception{
        List<Element> res = new ArrayList<>();
        Field[] fields = objectClass.getFields();
        for (Field field: fields){
            Element root = new Element("field");
            Element value;
            Object fieldVal = field.get(obj);
            if (fieldVal != null){
                // Handle the case when this field is primitive
                if (fieldVal.getClass().isPrimitive()){
                    value = new Element("value");
                    value.setText(fieldVal.toString());
                    root.addContent(value);
                } else {
                    // This is an object
                    value = new Element("reference");
                    value.setText(String.valueOf(fieldVal.hashCode()));
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
