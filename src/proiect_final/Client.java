/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proiect_final;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author pinte
 */
public class Client {

    public static final String IP = "127.0.0.1";
    private static final int PORT = 9876;
    private static int clientServerPort = PORT - 1;
    private static PrintWriter output;
    public static ArrayList<PrintWriter> connectedPortWriters = new ArrayList<>();

    private static void createOwnServer() throws IOException {
        ServerSocket listener;
        while (true) {
            try {
                listener = new ServerSocket(clientServerPort);
                break;
            } catch (BindException ex) {
                clientServerPort--;
            }
        }
        new Thread(new ServerListener(listener)).start();
    }

    private static void connectToMainServer() {
        Socket socket;
        try {
            socket = new Socket(IP, PORT);
            ServerConnection serverConn = new ServerConnection(socket);
            new Thread(serverConn).start();

            output = new PrintWriter(socket.getOutputStream(), true);
            output.println("PORT:" + clientServerPort);
        } catch (IOException ex) {
        }
    }

    private static void startCommandListener() {
        BufferedReader clientInput = new BufferedReader(new InputStreamReader(System.in));
        new Thread(new CommandHandler(clientInput, output)).start();

    }

    public static void main(String[] args) throws IOException {
        createOwnServer();
        connectToMainServer();
        startCommandListener();
    }

    public static class CommandHandler implements Runnable {

        BufferedReader input;
        PrintWriter output;

        public CommandHandler(BufferedReader input, PrintWriter output) {
            this.input = input;
            this.output = output;
        }

        private boolean isValidAddMessageCommand(String command) {
            return command.startsWith("adauga_mesaj(") && command.charAt(command.length() - 1) == ')';
        }

        private boolean isValidMyMessagesCommand(String command) {
            return command.equals("mesajele_mele");
        }

        private void handleAddMessageCommand(String command) {
            command = command.replace("adauga_mesaj(", "");
            command = command.replace(")", "");
            command = command.replace(" ", "");
            String[] params = command.split(",");
            if (params.length == 2) {
                Message message = Message.add(params[0], Integer.parseInt(params[1]));
                if (message == null) {
                    System.out.print("Categorie inexistenta! Folositi comanda 'categorii' pentru a va actualiza lista de categorii.\n> ");
                } else {
                    System.out.print("A fost adaugat mesajul '" + message.text + "' in categoria " + message.category.name + ", ID: " + message.id + "\n> ");
                }
            } else {
                System.out.print("Parametrii incorecti!\n> ");
            }
        }

        private void handleMyMessagesCommand() {
            System.out.print("Mesaje:");
            System.out.print(Message.list().replace('\t', '\n') + "\n> ");
        }

        private boolean isValidEmitNewsCommand(String command) {
            return command.startsWith("emite_stiri(") && command.charAt(command.length() - 1) == ')';
        }

        private void handleEmitNewsCommand(String command) {
            command = command.replace("emite_stiri(", "");
            command = command.replace(")", "");
            String[] messages = command.split(",");
            for (String messageHash : messages) {
                Message message = Message.find(Integer.parseInt(messageHash.trim()));
                if (message != null) {
                    for (PrintWriter portOutput : Client.connectedPortWriters) {
                        portOutput.println("stire(" + message.category.id + ", " + message.text + ")");
                    }
                }
            }
        }

        @Override
        public void run() {
            String command;
            while (true) {
                try {
                    command = input.readLine();
                    if (command.isBlank()) {
                        System.out.print("> ");
                        continue;
                    }
                    if (isValidAddMessageCommand(command)) {
                        handleAddMessageCommand(command);
                        continue;
                    }
                    if (isValidMyMessagesCommand(command)) {
                        handleMyMessagesCommand();
                        continue;
                    }
                    if (isValidEmitNewsCommand(command)) {
                        handleEmitNewsCommand(command);
                        continue;
                    }
                    output.println(command);

                } catch (IOException ex) {
                }
            }
        }
    }

    public static class ServerListener implements Runnable {

        ServerSocket listener;

        public ServerListener(ServerSocket listener) {
            this.listener = listener;
        }

        @Override
        public void run() {
            while (true) {
                Socket client;
                try {
                    client = listener.accept();
                    new Thread(new ClientClientHandler(client)).start();
                } catch (IOException ex) {
                }
            }
        }

    }
}
