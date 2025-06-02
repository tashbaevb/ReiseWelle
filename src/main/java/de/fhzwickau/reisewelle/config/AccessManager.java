package de.fhzwickau.reisewelle.config;

import java.util.Set;

public class AccessManager {

    private static Set<String> permissions;

    public static void setPermissions(Set<String> perms) {
        permissions = perms;
    }

    public static boolean hasPermission(String permission) {

        return permissions != null && permissions.contains(permission);
    }
}
