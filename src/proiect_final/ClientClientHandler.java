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

/**
 *
 * @author pinte
 */
public class ClientClientHandler implements Runnable {

    private Socket client;
    private BufferedReader input;
    private PrintWriter output;

    public ClientClientHandler(Socket client) {
        try {
            this.client = client;
            input = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
            output = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException ex) {
        }
    }

    private boolean isGetMessagesCommand(String command) {
        return command.equals("preia_mesaje");
    }

    private void handleGetMessagesCommand() {
        String messageList = Message.list();
        if (!messageList.equals("\tNu ai mesaje inregistrate!")) {
            output.println(Message.list());
        } else {
            output.println("skip");
        }
    }

    private boolean isValidReceivedNews(String command) {
        return command.startsWith("stire(") && command.charAt(command.length() - 1) == ')';
    }

    private void receivedNews(String command) {
        command = command.replace("stire(", "");
        command = command.replace(")", "");
        
        String[] params = command.split(",");
        System.out.println(params);
        Category category = Category.find(Integer.parseInt(params[0].trim()));
        if (category == null) return;

        for (ClientHandler aClient : Dispecer.clients) {
            if (aClient.user != null && aClient.user.isSubscribedTo(category)) {
                aClient.output.println("[NEWS] " + category.name + " | " + params[1]);
            }
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String command = input.readLine();
                System.out.println("CMD: " + command);
                if (isGetMessagesCommand(command)) {
                    handleGetMessagesCommand();
                    continue;
                }
                if (isValidReceivedNews(command)) {
                    receivedNews(command);
                }
            }
        } catch (IOException ex) {
        } finally {
            output.close();
            try {
                input.close();
            } catch (IOException ex) {
            }
        }
    }
}
