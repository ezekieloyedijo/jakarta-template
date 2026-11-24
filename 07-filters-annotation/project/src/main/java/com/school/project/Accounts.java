package com.school.project;

import java.util.Map;

/**
 * THE MEMBERS: who may log in. Kept in code to keep this topic simple.
 * ⚠️ Real apps NEVER keep plain passwords like this. Topic 13 shows hashing, topic 14 uses a database.
 */
public final class Accounts {

    // username -> { password, full name }
    private static final Map<String, String[]> MEMBERS = Map.of(
            "teacher", new String[]{"teach123", "Mrs. Bello"},
            "prefect", new String[]{"prefect123", "Head Prefect"});

    private Accounts() {
    }

    /** The member's full name if the username and password are right, otherwise null. */
    public static String check(String username, String password) {
        String[] member = MEMBERS.get(username == null ? "" : username.trim().toLowerCase());
        if (member != null && member[0].equals(password)) {
            return member[1];
        }
        return null;
    }
}
