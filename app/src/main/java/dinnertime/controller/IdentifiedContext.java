package dinnertime.controller;

import dinnertime.model.User;

/**
 * Store the User object of the logged in user that is local to this thread
 * @author aaron.mitchell
 */
public class IdentifiedContext {
    private static final ThreadLocal<User> local = new ThreadLocal<>();

    public static User get(){
        return local.get();
    }

    public static void set(User user){
        local.set(user);
    }

    public static void remove(){
        local.remove();
    }
}
