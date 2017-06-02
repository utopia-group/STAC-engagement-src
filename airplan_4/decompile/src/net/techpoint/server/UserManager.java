/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.server;

import java.util.HashMap;
import java.util.Map;
import net.techpoint.server.User;
import net.techpoint.server.UserFailure;

public class UserManager {
    Map<String, User> usersByUsername = new HashMap<String, User>();
    Map<String, User> usersByIdentity = new HashMap<String, User>();

    public void addUser(User user) throws UserFailure {
        if (this.usersByUsername.containsKey(user.obtainUsername())) {
            throw new UserFailure(user, "already exists");
        }
        this.usersByUsername.put(user.obtainUsername(), user);
        this.usersByIdentity.put(user.takeIdentity(), user);
    }

    public User fetchUserByUsername(String username) {
        return this.usersByUsername.get(username);
    }

    public User grabUserByIdentity(String identity) {
        return this.usersByIdentity.get(identity);
    }
}

