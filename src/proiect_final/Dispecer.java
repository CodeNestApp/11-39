package proiect_final;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author pinte
 */
public class Dispecer extends Thread {

    private static final int PORT = 9876;
    public static final ArrayList<ClientHandler> clients = new ArrayList<>();
    private static ServerSocket listener;

    private static void startServer() throws IOException {
        listener = new ServerSocket(PORT);
        log("Serverul a fost pornit, PORT: " + PORT);
    }

    private static void handleNewConnection(Socket client) {
        log("Un client s-a conectat!");
        ClientHandler clientThread = new ClientHandler(client);
        clients.add(clientThread);
        new Thread(clientThread).start();
    }

    private static void handleConnections() throws IOException {
        Socket client = listener.accept();
        handleNewConnection(client);
    }

    public static void main(String[] args) throws IOException {
        startServer();

        while (true) {
            handleConnections();
        }
    }

    private static void log(String message) {
        System.out.println("[SERVER] " + message);
    }
}
