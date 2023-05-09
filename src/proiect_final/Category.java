package proiect_final;

import java.util.ArrayList;

/**
 *
 * @author pinte
 */
public class Category {

    int id;
    String name;
    public static ArrayList<Category> all = new ArrayList<>();

    public Category(String name) {
        this.name = name;
        this.id = Category.all.size();
        Category.all.add(this);
    }

    public Category(int id, String name) {
        this.name = name;
        this.id = id;
        Category.all.add(this);
    }

    public static Category add(String name) {
        // TODO Make sure that the name is unique
        Category createdCategory = new Category(name);

        return createdCategory;
    }

    public static String list() {
        if (Category.all.isEmpty()) {
            return "Nu exista categorii!";
        }
        String categories = "Categorii:";
        for (Category category : Category.all) {
            categories += "\t Categoria " + category.id + ") " + category.name;
        }
        return categories;
    }
    
    public static Category find(int findById) {
        Category foundCategory = null;
        for (Category category : all) {
            if (category.id == findById) {
                foundCategory = category;
                break;
            }
        }
        
        return foundCategory;
    }
    
    public static void addForUser(int id, String name) {
        new Category(id, name); 
    }
}
