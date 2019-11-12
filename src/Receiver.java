import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class Receiver  {
    private static final int PORT = 1107;

    public static void main(String[] args){
        try {
            ServerSocket listener = new ServerSocket(1107);
            System.out.println("Connection authentication: ");
            System.out.println(" - IP: " + InetAddress.getLocalHost().getHostAddress());
            System.out.println(" - PORT: " + PORT);
            Receiver receiver = new Receiver();
            Deserializer deserializer = new Deserializer();

            while(true){
                System.out.println("Waiting for the client request...");
                // Creating socket and waiting for client connection
                Socket socket = listener.accept();
                // Read from socket to InputStream object
                DataInputStream in = new DataInputStream(socket.getInputStream());
                // Convert InputStream object to byte[]
                int length = in.readInt();
                if(length>0) {
                    byte[] docBytes = new byte[length];
                    in.readFully(docBytes, 0, docBytes.length);
                    // Convert and deserialize
                    System.out.println("================================= RESULT =================================");
                    deserializer.deserialize(receiver.bytesToDoc(docBytes));
                    break;
                }
                //close resources
                in.close();
                socket.close();
            }
            System.out.println("Shutting down socket server");
            //close the ServerSocket object
            listener.close();
        } catch (java.lang.Exception ex) {
            ex.printStackTrace(System.out);
        }
    }


    private Document bytesToDoc(byte[] byteArray) throws Exception{
        ByteArrayInputStream byteStream = new ByteArrayInputStream(byteArray);
        SAXBuilder builder = new SAXBuilder();
        return builder.build(byteStream);
    }

}
