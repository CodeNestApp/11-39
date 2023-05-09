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
public class ClientHandler implements Runnable {

    private Socket client;
    private BufferedReader input;
    PrintWriter output;
    User user = null;
    int clientServerPort;

    public ClientHandler(Socket client) {
        try {
            this.client = client;
            input = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
            output = new PrintWriter(client.getOutputStream(), true);
            output.println("Bun venit! Tastati help pentru lista de comenzi.");
        } catch (IOException ex) {
        }
    }

    private boolean isValidLoginCommand(String command) {
        return command.startsWith("login(") && command.charAt(command.length() - 1) == ')';
    }

    private void handleLoginCommand(String command) {
        command = command.replace("login(", "");
        command = command.replace(")", "");
        command = command.replace(" ", "");
        String[] params = command.split(",");
        if (params.length == 2) {
            if (user == null) {
                user = User.find(params[0], params[1]);
                if (user == null) {
                    output.println("Login esuat, verificati numele si parola!");
                } else {
                    successLoginOrRegister();
                }
            } else {
                output.println("Sunteti deja logat!");
            }
        } else {
            output.println("Comanda incorecta!");
        }
    }

    private boolean isValidRegisterCommand(String command) {
        return command.startsWith("register(") && command.charAt(command.length() - 1) == ')';
    }

    private void handleRegisterCommand(String command) {
        command = command.replace("register(", "");
        command = command.replace(")", "");
        command = command.replace(" ", "");
        String[] params = command.split(",");
        if (params.length == 2) {
            if (user == null) {
                user = User.add(params[0], params[1]);
                if (user == null) {
                    output.println("Inregistrare esuata, numele este deja inregistrat!");
                } else {
                    successLoginOrRegister();
                }
            } else {
                output.println("Sunteti deja logat!");
            }
        } else {
            output.println("Comanda incorecta!");
        }
    }

    private void successLoginOrRegister() {
        String welcomeMsg = "Bun venit, " + user.name + "! Tastati help pentru lista de comenzi.\t";
        user.port = clientServerPort;
        welcomeMsg += User.list();

        output.println(welcomeMsg);
        output.println("PORTS:" + User.ports());
    }

    private void handleHelpCommand() {
        String help = "Comenzi disponibile:";

        if (user == null) {
            help += "\tlogin(nume, parola) - Conectare in sistem";
            help += "\tregister(nume, parola) - Inregistrare in sistem";
        } else {
            help += "\tcategorii - Afiseaza si Actualizeaza local lista de categorii";
            help += "\tadauga_categorie(nume) - Adauga o categorie";
            help += "\tadauga_mesaj(text, id_categorie) - Adauga un mesaj";
            help += "\tmesajele_mele - Afiseaza mesajele proprii";
            help += "\tsubscribe(id_categorie) - Subscribe la categorie";
            help += "\tanunta(id_categorie, mesaj) - Anunta o categorie";
        }

        output.println(help);
    }

    private void handleUnauthorized() {
        output.println("Access restrictionat!");
    }

    private void handleCategoriesCommand() {
        output.println(Category.list().replace("\t", "\n"));
    }

    private boolean isValidAnnounceCommand(String command) {
        return command.startsWith("anunta(") && command.charAt(command.length() - 1) == ')';
    }

    private void handleAnnounceCommand(String command) {
        command = command.replace("anunta(", "");
        command = command.replace(")", "");
        command = command.replace(" ", "");
        String[] params = command.split(",");
        if (params.length == 2) {
            Category category = Category.find(Integer.parseInt(params[0]));
            if (category == null) {
                output.println("Categorie inexistenta!");
            } else {
                announce(category, params[1]);
                output.print("\n> ");
            }
        } else {
            output.println("Comanda incorecta!");
        }
    }

    private boolean isValidAddCategoryCommand(String command) {
        return command.startsWith("adauga_categorie(") && command.charAt(command.length() - 1) == ')';
    }

    private void handleAddCategoryCommand(String command) {
        command = command.replace("adauga_categorie(", "");
        command = command.replace(")", "");
        String categoryName = command;

        if (categoryName.equals("")) {
            output.println("Pentru a adauga o categorie, trebuie sa ii specificati numele!");
        } else {
            Category category = Category.add(categoryName);
            output.println("Categoria " + category.name + " a fost adaugata. ID: " + category.id);
        }
    }

    private boolean isValidSubscribeCommand(String command) {
        return command.startsWith("subscribe(") && command.charAt(command.length() - 1) == ')';
    }

    private void handleSubscribeCommand(String command) {
        command = command.replace("subscribe(", "");
        command = command.replace(")", "");
        int categoryId = Integer.parseInt(command);

        Category category = Category.find(categoryId);
        if (category == null) {
            output.println("Categorie inexistenta!");
        } else {
            user.subscribe(category.id);
            output.println("Ati fost abonat cu succes la categoria " + category.name);
        }
    }

    private void announce(Category category, String message) {
        for (ClientHandler aClient : Dispecer.clients) {
            if (aClient.user != null && aClient.user.isSubscribedTo(category)) {
                aClient.output.println("Anunt nou in " + category.name + " de la " + user.name + ": " + message);
            }
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String command = input.readLine();
                if (command == null || command.isBlank()) continue;
                if (command.startsWith("PORT:")) {
                    clientServerPort = Integer.parseInt(command.replace("PORT:", ""));
                    continue;
                }
                if (isValidLoginCommand(command)) {
                    handleLoginCommand(command);
                    continue;
                }
                if (isValidRegisterCommand(command)) {
                    handleRegisterCommand(command);
                    continue;
                }
                if (command.equals("help")) {
                    handleHelpCommand();
                    continue;
                }
                if (user == null) {
                    handleUnauthorized();
                    continue;
                }
                if (command.equals("categorii")) {
                    handleCategoriesCommand();
                    continue;
                }
                if (isValidAnnounceCommand(command)) {
                    handleAnnounceCommand(command);
                    continue;
                }
                if (isValidAddCategoryCommand(command)) {
                    handleAddCategoryCommand(command);
                    continue;
                }
                if (isValidSubscribeCommand(command)) {
                    handleSubscribeCommand(command);
                    continue;
                }
                output.println("Comanda incorecta!");
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
