package eu.alican.topoo.models;

/**
 * Project: Topoo
 * Created by alican on 14.03.2016.
 */
public class User {

    long id;
    String name;
    boolean admin;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        String label = name;
        if(this.id == 1){
            label += " (Admin)";
        }
        return label;
    }
}
