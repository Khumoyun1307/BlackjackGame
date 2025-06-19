package com.blackjack.gui;

import com.blackjack.user.PlayerProfile;
import com.blackjack.user.UserManager;

public class ApplicationContext {
    private static PlayerProfile currentProfile;
    private static final UserManager userManager = new UserManager();

    public static PlayerProfile getProfile() {
        return currentProfile;
    }
    public static void setProfile(PlayerProfile profile) {
        currentProfile = profile;
    }
    public static UserManager getUserManager() {
        return userManager;
    }
}
