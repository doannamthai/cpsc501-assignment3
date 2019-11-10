import objects.*;

import java.lang.reflect.Field;
import java.util.Queue;
import java.util.Scanner;

public class ObjectCreator {

    public static void main(String[] args) {

    }

    public SupportObject createObject() {
        try {
            return getObject(0);
        } catch (Exception e){
            System.err.println("Cannot create object");
            e.printStackTrace();
        }
        return null;
    }

    private SupportObject getObject(int depth) throws Exception{
        SupportObject supportObject = null;
        Scanner scanner = new Scanner(System.in);
        int input = 0;
        String indentation = Utils.getIndentation(depth);
        System.out.format("%s Please select the below options\n", indentation);
        System.out.format(" %s(1) Simple Object\n", indentation);
        System.out.format(" %s(2) Object with references to other objects\n", indentation);
        System.out.format(" %s(3) Object with array of primitives\n", indentation);
        System.out.format(" %s(4) Object with array of references\n", indentation);
        System.out.format(" %s(5) Object using collection interface\n", indentation);
        System.out.format(" %s(6) Terminate\n", indentation);
        System.out.format(" %sYour selection: ", indentation);
        input = scanner.nextInt();
        if (input == 6) return null;
        supportObject = selectOptions(input, depth);

        return supportObject;
    }

    private SupportObject selectOptions(int input, int depth) throws  Exception{
        switch(input){
            case 1:
                return optionsForSimpleObject(depth);
            case 2:
                return optionsForObjectWithReference(depth);
            case 3:
                return optionsForObjectWithPrimitiveArray(depth);
            case 4:
                return optionsForObjectWithObjectArray(depth);
            case 5:
                return optionsForObjectWithCollection(depth);
        }
        return null;
    }

    public SupportObject optionsForSimpleObject(int depth) throws Exception{
        Scanner scanner = new Scanner(System.in);
        SimpleObject simpleObject = new SimpleObject();
        Class simpleObjectClass = Class.forName("objects.SimpleObject");
        String indentation = Utils.getIndentation(depth);
        System.out.format("%sYou are selecting a simple object. Enter a number OR \"N\" to ignore this field\n", indentation);
        for (Field field : simpleObjectClass.getDeclaredFields()){
            System.out.format("%sValue for %s: ", indentation, field.getName());
            String input = scanner.next();
            if (!input.equals("N")){
                field.set(simpleObject, Double.parseDouble(input));
            }
        }
        return simpleObject;
    }

    public SupportObject optionsForObjectWithReference(int depth) throws Exception{
        Scanner scanner = new Scanner(System.in);
        ObjectWithReference objectWithReference = new ObjectWithReference();
        Class objectClass = Class.forName("objects.ObjectWithReference");
        String indentation = Utils.getIndentation(depth);
        System.out.format("%sYou are selecting a object with references. " +
                "Select a option from 1-5 to create the object OR \"N\" to ignore this field\n", indentation);
        for (Field field : objectClass.getDeclaredFields()){
            System.out.format("%sValue for %s: ", indentation, field.getName());
            String input = scanner.next();
            if (!input.equals("N")){
                int inputVal = Integer.parseInt(input);
                if (inputVal > 5 || inputVal < 1) throw new Exception("Invalid options");
                field.set(objectWithReference, selectOptions(inputVal, depth+1));
            }
        }
        return objectWithReference;
    }

    public SupportObject optionsForObjectWithPrimitiveArray(int depth) throws Exception{
        Scanner scanner = new Scanner(System.in);
        ObjectWithPrimitiveArray objectWithPrimitiveArray = new ObjectWithPrimitiveArray();
        String indentation = Utils.getIndentation(depth);
        System.out.format("%sYou are selecting an object with primitive array. " +
                "Enter the size of the array and fill in array with your primitives\n", indentation);
        System.out.format("%sEnter the size of array: ", indentation);
        int size = scanner.nextInt();
        double[] array = new double[size];
        for (int i = 0; i < size; i++){
            System.out.format("%sValue for element at index %d: ", indentation, i);
            Double val = scanner.nextDouble();
            array[i] = val;
        }
        // Set the value for the created object
        objectWithPrimitiveArray.setNumbers(array);
        return objectWithPrimitiveArray;
    }

    public SupportObject optionsForObjectWithObjectArray(int depth) throws Exception{
        Scanner scanner = new Scanner(System.in);
        ObjectWithObjectArray objectWithObjectArray = new ObjectWithObjectArray();
        Class objectClass = Class.forName("objects.ObjectWithObjectArray");
        String indentation = Utils.getIndentation(depth);
        System.out.format("%sYou are selecting an object with object array. " +
                "Enter the size of the array and fill in array with your objects\n", indentation);
        System.out.format("%sEnter the size of array: ", indentation);
        int size = scanner.nextInt();
        SupportObject[] array = new SupportObject[size];
        System.out.format("%sSelect an option from 1-5 to create an object: \n", indentation);
        for (int i = 0; i < size; i++){
            System.out.format("%sObject at index %d: ", indentation, i);
            int inputVal = scanner.nextInt();
            if (inputVal > 5 || inputVal < 1) throw new Exception("Invalid options");
            SupportObject object = selectOptions(inputVal, depth+1);
            array[i] = object;
        }
        // Set the value for the created object
        objectClass.getDeclaredField("supportObjects").set(objectWithObjectArray, array);
        return objectWithObjectArray;
    }

    public SupportObject optionsForObjectWithCollection(int depth) throws Exception{
        Scanner scanner = new Scanner(System.in);
        ObjectWithCollection objectWithCollection = new ObjectWithCollection();
        Queue<SupportObject> queue = objectWithCollection.getQueue();
        String indentation = Utils.getIndentation(depth);
        System.out.format("%sYou are selecting an object with Java collection.\n" +
                "Choose an option from 1-5 to create object OR OR \"E\" when you are done\n", indentation);
        String input;
        do {
            System.out.format("%sObject (1-5 OR \"N\"): ", indentation);
            input = scanner.next();
            if (input.equals("E")) break;
            else {
                int inputVal = Integer.valueOf(input);
                if (inputVal > 5 || inputVal < 1) throw new Exception("Invalid options");
                queue.add(selectOptions(inputVal, depth+1));
            }
        } while (true);
        objectWithCollection.setQueue(queue);
        return objectWithCollection;
    }


}
