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
    private String host = null;
    private int port = 0;
    public static void main(String[] args) throws Exception{
        Sender sender = new Sender();
        while(true){
            ObjectCreator creator = new ObjectCreator();
            Serializer serializer = new Serializer();
            SupportObject obj = creator.createObject();
            if (obj == null) {
                System.out.println("TERMINATING...");
                return;
            }
            System.out.println("SERIALIZING...");
            System.out.println("=========================== RESULT ===========================");
            Document doc = serializer.serialize(obj);
            // Sender
            System.out.println("SENDING TO THE RECEIVER...");
            sender.connect(doc);
            System.out.println("SENT");
            System.out.println("======================================================");
        }

    }

    private void connect(Document doc) throws Exception{
        if (host == null || port == 0){
            Scanner scanner = new Scanner(System.in);
            System.out.print("Server IP: ");
            host = scanner.next().trim();
            System.out.print("Server PORT: ");
            port = scanner.nextInt();
        }
        Socket socket = new Socket(host, port);
        sendBytes(socket, convertDocToByte(doc));
        socket.close();
    }

    private void sendBytes(Socket socket, byte[] bytes) throws Exception{
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeInt(bytes.length);
        out.write(bytes);
        out.close();
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
