package server;

import common.Utils;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class ClientListener implements Runnable {

    private boolean running;
    private final Socket socket;
    private final String username;
    private final Server server;

    public ClientListener(String username, Socket socket, Server server) {
        this.server = server;
        running = false;
        this.socket = socket;
        this.username = username;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        running = true;
        String message;
        while (running) {
            message = Utils.receiveMessage(socket);
            if (message.toLowerCase().equals("quit")) {
                server.getClientes().remove(username);
                try {
                    socket.close();
                } catch (IOException ex) {
                    System.err.println("[ClientListener:Run] -> " + ex.getMessage());
                }
                running = false;
            } else if (message.equals("GET_CONNECTED_USERS")) {
                System.out.println("Request to update contact list...");
                String response = "";
                for (Map.Entry<String, ClientListener> pair : server.getClientes().entrySet()) {
                    response += (pair.getKey() + ";");
                }
                Utils.sendMessage(socket, response);
            }
            System.out.println(" >> Message: " + message);
        }
    }

}
