/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proiect_final;

import java.util.ArrayList;
/**
 *
 * @author pinte
 */
public class Message {

    Category category;
    String text;
    int id;
    private static ArrayList<Message> all = new ArrayList<>();
    
    
    public Message(String text, Category category) {
        this.category = category;
        this.text = text;
        this.id = text.hashCode();
        all.add(this);
    }

    public static Message add(String text, int category_id) {
        Category category = Category.find(category_id);
        if (category == null) {
            return null;
        }
        return new Message(text, category);
    }

    public static String list() {
        if (Message.all.isEmpty()) {
            return "\tNu ai mesaje inregistrate!";
        }
        String messages = "";
        for (Message message : Message.all) {
            messages += "-> " + message.category.name + " | " + message.id + "\t";
        }
        return messages;
    }
    
    public static Message find(int findById) {
        Message foundMessage = null;
        for (Message message : all) {
            if (message.id == findById) {
                foundMessage = message;
                break;
            }
        }
        
        return foundMessage;
    }
}
