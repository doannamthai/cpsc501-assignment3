package receiver;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Receiver  {
    private static final int PORT = 1107;

    public static void main(String[] args){
        try {
            ServerSocket listener = new ServerSocket(PORT);
            System.out.println("Connection authentication: ");
            System.out.println(" - IP: " + InetAddress.getLocalHost().getHostAddress());
            System.out.println(" - PORT: " + PORT);
            System.out.println("Enter E to terminate the program");
            Deserializer deserializer = new Deserializer();
            ExecutorService pool = Executors.newFixedThreadPool(20);
            while (true) {
                System.out.println("Waiting for the client request...");
                pool.execute(new Runner(listener.accept(), deserializer));
            }
            //listener.close();
           // System.out.println("Shutting down socket server");
            //close the ServerSocket object
        } catch (java.lang.Exception ex) {
            ex.printStackTrace(System.out);
        }
    }


    private static Document bytesToDoc(byte[] byteArray) throws Exception{
        ByteArrayInputStream byteStream = new ByteArrayInputStream(byteArray);
        SAXBuilder builder = new SAXBuilder();
        return builder.build(byteStream);
    }

    private static class Runner implements Runnable {
        private Socket socket;
        private Deserializer deserializer;

        Runner(Socket socket, Deserializer deserializer) {
            this.socket = socket;
            this.deserializer = deserializer;
        }

        @Override
        public void run() {
            System.out.println("Connected: " + socket);
            try {
                // Read from socket to InputStream object
                DataInputStream in = new DataInputStream(socket.getInputStream());
                // Convert InputStream object to byte[]
                int length = in.readInt();
                if(length>0) {
                    byte[] docBytes = new byte[length];
                    in.readFully(docBytes, 0, docBytes.length);
                    // Convert and deserialize
                    System.out.println("================================= BEGIN OF RESULT =================================");
                    deserializer.deserialize(bytesToDoc(docBytes));
                    System.out.println("================================= END OF RESULT =================================");
                }
                //close resources
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error:" + socket);
            } finally {
                try { socket.close(); } catch (IOException e) {}
                System.out.println("Closed: " + socket);
            }
        }
    }

}
