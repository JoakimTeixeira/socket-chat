package common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Utils {
    
    // sends message to a certain connection
    public static boolean sendMessage(Socket sock, String message)
    {     
        try {
            ObjectOutputStream output = new ObjectOutputStream(sock.getOutputStream());
            output.flush();
            output.writeObject(message);
            return true;
        } catch (IOException ex) {
            System.err.println("[ERROR:sendMessage] -> " + ex.getMessage());
        }
        return false;
    }
    
    // functionality to receive message
    public static String receiveMessage(Socket sock)
    {
        String response = null;
        try {
            ObjectInputStream input = new ObjectInputStream(sock.getInputStream());
            response = (String) input.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println("[ERROR:receiveMessage] -> " + ex.getMessage());
        }
         return response;
    }
}
