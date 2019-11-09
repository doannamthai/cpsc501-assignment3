import objects.SimpleObject;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Scanner;

public class ObjectCreator {

    public static void main(String[] args) throws Exception{
        ObjectCreator creator = new ObjectCreator();
        creator.displayMainMenu();
    }

    public void displayMainMenu() throws Exception{
        Scanner scanner = new Scanner(System.in);
        int input = 0;
        do {

            System.out.println("Please select the below options");
            System.out.println(" (1) Simple Object");
            System.out.println(" (2) Object with references to other objects");
            System.out.println(" (3) Object with array of primitives");
            System.out.println(" (4) Object with array of references");
            System.out.println(" (5) Object using collection interface");
            System.out.println(" (6) Exit");

            input = scanner.nextInt();

            switch(input){
                case 1: {
                    optionsForSimpleObject();
                }
            }

        } while (input != 6);
    }

    public void optionsForSimpleObject() throws Exception{
        Scanner scanner = new Scanner(System.in);
        SimpleObject simpleObject = new SimpleObject();
        Class simpleObjectClass = Class.forName("SimpleObject");
        System.out.println("You are selecting a simple object. Enter a number for each field OR \"N\" for nothing");
        for (Field field : simpleObjectClass.getDeclaredFields()){
            System.out.println(MessageFormat.format("Value for {0}: ", field.getName()));
            String input = scanner.next();
            if (!input.equals("N")){
                //field.set(simpleObject, NumberFormat.get);
            }
        }
    }


}
