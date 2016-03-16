package eu.alican.topoo.app;

import android.app.Application;

import eu.alican.topoo.models.User;

/**
 * Project: Topoo
 * Created by alican on 15.03.2016.
 */
public class TopooApplication extends Application {
    User user;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public boolean isAuthenticated(){
        return (user != null);
    }

    public User getUser(){
        return user;
    }

    public boolean isAdmin(){
        return user != null && user.getId() == 1;
    }


    public void authenticateUser(User user){
        this.user = user;
    }

    public void logoutUser(){
        this.user = null;
    }
}
