/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proiect_final;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author pinte
 */
public class ServerConnection implements Runnable {

    private Socket server;
    private BufferedReader input;

    public ServerConnection(Socket serverSocket) throws IOException {
        server = serverSocket;
        input = new BufferedReader(new InputStreamReader(server.getInputStream()));
    }

    private boolean isPortsServerResponse(String serverResponse) {
        return serverResponse.startsWith("PORTS:");
    }

    private void handlePortsServerResponse(String serverResponse) throws IOException {
        serverResponse = serverResponse.replace("PORTS:", "");
        String[] ports = serverResponse.split(",");
        System.out.print("\nMesaje: ");
        for (String port : ports) {
            Socket socket = new Socket(Client.IP, Integer.parseInt(port));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            Client.connectedPortWriters.add(output);
            output.println("preia_mesaje");

            new Thread(new ServerConnection(socket)).start();
        }
    }

    private boolean isCategoryServerResponse(String serverResponse) {
        return serverResponse.startsWith(" Categoria");
    }

    private void handleCategoryServerResponse(String serverResponse) {
        List<String> categoryDetails = Arrays.asList(serverResponse.replace(" Categoria ", "").split("\\)"));
        Category.addForUser(Integer.parseInt(categoryDetails.get(0)), categoryDetails.get(1).replace(" ", ""));
        System.out.print(serverResponse.replace("\t", "\n"));
        System.out.print("\n> ");
    }
    
    private void handleDefaultServerResponse(String serverResponse) {
        System.out.print(serverResponse.replace("\t", "\n"));
        System.out.print("\n> ");
    }

    @Override
    public void run() {
        String serverResponse;
        try {
            while (true) {
                serverResponse = input.readLine();
                if (isPortsServerResponse(serverResponse)) {
                    handlePortsServerResponse(serverResponse);
                    continue;
                }
                if (isCategoryServerResponse(serverResponse)) {
                    handleCategoryServerResponse(serverResponse);
                    continue;
                }
                if (serverResponse.equals("skip")) {
                    System.out.print("\n> ");
                } else {
                    handleDefaultServerResponse(serverResponse);
                }

            }
        } catch (IOException ex) {
        } finally {
            try {
                input.close();
            } catch (IOException ex) {
            }

        }

    }
}
