import objects.SupportObject;
import org.jdom2.Document;
import org.jdom2.transform.JDOMSource;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Sender {
    // host and port of receiver

    public static void main(String[] args) throws Exception{
        ObjectCreator creator = new ObjectCreator();
        Serializer serializer = new Serializer();
        SupportObject obj = creator.createObject();
        if (obj == null) throw new Exception("Couldn't create object, process is terminated");
        System.out.println("SERIALIZING...");
        System.out.println("=========================== RESULT ===========================");
        Document doc = serializer.serialize(obj);
        // Sender
        System.out.println("SENDING TO THE RECEIVER...");
        Sender sender = new Sender();
        sender.connect(doc);
    }

    private void connect(Document doc) throws Exception{
        Scanner scanner = new Scanner(System.in);
        System.out.print("Server IP: ");
        String ip = scanner.next().trim();
        System.out.print("Server PORT: ");
        int port = scanner.nextInt();
        Socket socket = new Socket(ip, port);
        sendBytes(socket, convertDocToByte(doc));
    }

    private void sendBytes(Socket socket, byte[] bytes) throws Exception{
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeInt(bytes.length);
        out.write(bytes);
    }

    private byte[] convertDocToByte(Document doc) throws Exception{
        Source source = new JDOMSource(doc);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(bos);
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.transform(source, result);
        byte[] array = bos.toByteArray();
        return array;
    }
}
