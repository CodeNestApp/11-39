package proiect_final;

import java.util.ArrayList;

/**
 *
 * @author pinte
 */
public class User {

    String name, password;
    static ArrayList<User> all = new ArrayList<>();
    ArrayList<Integer> category_ids = new ArrayList<>();
    int port;

    public User(String name, String password) {
        this.name = name;
        this.password = password;
        User.all.add(this);
    }

    public static User find(String name, String password) {
        User foundUser = null;
        for (User user : all) {
            if (user.name.equals(name) && user.password.equals(password)) {
                foundUser = user;
                break;
            }
        }

        return foundUser;
    }

    public static User find(String name) {
        User foundUser = null;
        for (User user : all) {
            if (user.name.equals(name)) {
                foundUser = user;
                break;
            }
        }

        return foundUser;
    }

    public static User add(String name, String password) {
        User foundUser = User.find(name);
        if (foundUser != null) {
            return null;
        }
        User createdUser = new User(name, password);

        return createdUser;
    }

    public void subscribe(int category_id) {
        category_ids.add(category_id);
    }

    public static String list() {
        String users = "Useri Conectati:";
        for (User user : all) {
            users += "\t -> " + user.name + ", PORT: " + user.port;
        }

        return users;
    }
    
    public static String ports() {
        String ports = "";
        for (User user : all) {
            ports += (user.port + ",");
        }

        return ports;
    }
    
    public boolean isSubscribedTo(Category category) {
        return category_ids.contains(category.id);
    }
}
