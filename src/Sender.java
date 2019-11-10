import objects.SupportObject;

public class Sender {
    public static void main(String[] args) throws Exception{
        ObjectCreator creator = new ObjectCreator();
        Serializer serializer = new Serializer();
        SupportObject obj = creator.createObject();
        if (obj == null) throw new Exception("Couldn't create object, process is terminated");
        serializer.serialize(obj);
    }
}
