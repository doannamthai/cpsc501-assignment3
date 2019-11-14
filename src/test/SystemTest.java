package test;
import static org.junit.Assert.*;

import objects.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.junit.Test;
import receiver.Deserializer;
import sender.Serializer;
import java.util.Arrays;
import java.util.List;


public class SystemTest {
    @Test
    public void testSimpleObjectSer() throws Exception{
        Serializer serializer = new Serializer();
        SimpleObject simpleObject = createSimpleObject(1,2,3);
        Element root = new Element("serialized");
        Element object = createDefaultSimpleObjectElement(0);
        root.addContent(object);
        Document expected = new Document(root);
        assertEquals(docToString(expected), docToString(serializer.serialize(simpleObject)));
    }

    @Test
    public void testObjectWithRefSer() throws Exception{
        Serializer serializer = new Serializer();
        ObjectWithReference objectWithReference = new ObjectWithReference();
        objectWithReference.object1 = createSimpleObject(1,2,3);
        // Serialization
        Element root = new Element("serialized");
        Element object = new Element("object");
        object.setAttribute("name", "objects.ObjectWithReference");
        object.setAttribute("id", "0");
        for (int i = 0; i < 3; i++){
            Element field = new Element("field");
            field.setAttribute("name", "object"+(i+1));
            field.setAttribute("declaringclass", "objects.SupportObject");
            if (i == 0){
                Element ref = new Element("reference");
                ref.setText(String.valueOf(1));
                field.addContent(ref);
            }
            object.addContent(field);
        }
        Element simpleObj = createDefaultSimpleObjectElement(1);
        root.addContent(object);
        root.addContent(simpleObj);
        assertEquals(docToString(new Document(root)), docToString(serializer.serialize(objectWithReference)));
    }

    @Test
    public void testObjectWithPrimitiveArraySer() throws Exception{
        Serializer serializer = new Serializer();
        ObjectWithPrimitiveArray main = new ObjectWithPrimitiveArray();
        main.numbers = new double[]{1,2,3};
        // Serialization
        Element root = new Element("serialized");
        Element object = new Element("object");
        object.setAttribute("name", "objects.ObjectWithPrimitiveArray");
        object.setAttribute("id", "0");
        Element field = new Element("field");
        field.setAttribute("name", "numbers");
        field.setAttribute("declaringclass", "[D");
        Element ref = new Element("reference");
        ref.setText("1");
        field.addContent(ref);
        object.addContent(field);
        Element arrayObj = new Element("object");
        arrayObj.setAttribute("name", "[D");
        arrayObj.setAttribute("id", "1");
        arrayObj.setAttribute("length", "3");
        for (int i = 0; i < 3; i++){
            Element val = new Element("value");
            val.setText(String.valueOf((double) i+1));
            arrayObj.addContent(val);
        }
        root.addContent(object);
        root.addContent(arrayObj);
        assertEquals(docToString(new Document(root)), docToString(serializer.serialize(main)));
    }

    @Test
    public void testSimpleObjectDes() throws Exception {
        SimpleObject simpleObject = createSimpleObject(1,2,3);
        Serializer serializer = new Serializer();
        Deserializer deserializer = new Deserializer();
        SimpleObject res = (SimpleObject) deserializer.deserialize(serializer.serialize(simpleObject));
        assertTrue(compareSimpleObject(simpleObject, res));
    }

    //@Test
    public void testObjectWithRefDes() throws Exception {
        ObjectWithReference objectWithReference = new ObjectWithReference();
        objectWithReference.object1 = createSimpleObject(1,2,3);
        Serializer serializer = new Serializer();
        Deserializer deserializer = new Deserializer();
        ObjectWithReference res = (ObjectWithReference) deserializer.deserialize(serializer.serialize(objectWithReference));
        SimpleObject expected = (SimpleObject)  objectWithReference.object1;
        SimpleObject actual = (SimpleObject) res.object1;
        assertTrue(compareSimpleObject(expected, actual));
    }

    @Test
    public void testObjectWithPrimitiveArrayDes() throws Exception{
        Serializer serializer = new Serializer();
        Deserializer deserializer = new Deserializer();
        ObjectWithPrimitiveArray main = new ObjectWithPrimitiveArray();
        main.numbers = new double[]{1,2,3};
        ObjectWithPrimitiveArray objectWithPrimitiveArray = (ObjectWithPrimitiveArray) deserializer.deserialize(serializer.serialize(main));
        assertTrue(Arrays.equals(main.numbers, objectWithPrimitiveArray.numbers));
    }

    @Test
    public void testObjectWithObjectArrayDes() throws Exception{
        Serializer serializer = new Serializer();
        Deserializer deserializer = new Deserializer();
        ObjectWithObjectArray objectWithObjectArray = new ObjectWithObjectArray();
        SimpleObject obj = createSimpleObject(1,2,3);
        objectWithObjectArray.supportObjects = new SupportObject[]{obj};
        ObjectWithObjectArray actual = (ObjectWithObjectArray) deserializer.deserialize(serializer.serialize(objectWithObjectArray));
        assertEquals(objectWithObjectArray.supportObjects.length, actual.supportObjects.length);
        assertTrue(compareSimpleObject((SimpleObject) objectWithObjectArray.supportObjects[0],
                (SimpleObject) actual.supportObjects[0]));
    }

    @Test
    public void testObjectWithCollection() throws Exception{
        Serializer serializer = new Serializer();
        Deserializer deserializer = new Deserializer();
        ObjectWithCollection objectWithCollection = new ObjectWithCollection();
        List<SupportObject> expectedList =  objectWithCollection.getList();
        expectedList.add(createSimpleObject(1,2,3));
        objectWithCollection.setList(expectedList);
        ObjectWithCollection actual = (ObjectWithCollection) deserializer.deserialize(serializer.serialize(objectWithCollection));
        System.out.println(expectedList);
        System.out.println(actual.getList());
        assertEquals(expectedList.size(), actual.getList().size());
        assertTrue(compareSimpleObject((SimpleObject) expectedList.get(0), (SimpleObject) actual.getList().get(0)));
    }

    private SimpleObject createSimpleObject(double val1, double val2, double val3){
        SimpleObject simpleObject = new SimpleObject();
        simpleObject.number1 = val1;
        simpleObject.number2 = val2;
        simpleObject.number3 = val3;
        return simpleObject;
    }

    private boolean compareSimpleObject(SimpleObject obj1, SimpleObject obj2){
        return obj1.number1 == obj2.number1 && obj1.number2 == obj2.number2 && obj1.number3 == obj2.number3;
    }


    private String docToString(Document doc){
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        return outputter.outputString(doc);
    }

    private Element createDefaultSimpleObjectElement(int id){
        // Simple object with val: 1,2,3
        Element object = new Element("object");
        object.setAttribute("name", "objects.SimpleObject");
        object.setAttribute("id", String.valueOf(id));
        for (int i = 0; i < 3; i++){
            Element field = new Element("field");
            field.setAttribute("name", "number"+(i+1));
            field.setAttribute("declaringclass", "double");
            Element value = new Element("value");
            value.setText(String.valueOf((double) (i+1)));
            field.addContent(value);
            object.addContent(field);
        }
        return object;
    }

}
