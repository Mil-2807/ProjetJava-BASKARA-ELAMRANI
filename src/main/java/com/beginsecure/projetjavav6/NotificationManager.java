package com.beginsecure.projetjavav6;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages all system notifications, providing a centralized storage and retrieval mechanism.
 */
public class NotificationManager {

    // A thread-safe list to store all notifications.
    private static final List<String> allNotifications = Collections.synchronizedList(new ArrayList<>());

    /**
     * Adds a new notification message to the manager.
     * @param notificationMessage The full text of the notification to add.
     */
    public static void addNotification(String notificationMessage) {
        if (notificationMessage != null && !notificationMessage.trim().isEmpty()) {
            allNotifications.add(notificationMessage);
            // Optional: Print to console for debugging, but UI will read from here.
            System.out.println("NOTIFICATION MANAGER: Added: " + notificationMessage);
        }
    }

    /**
     * Retrieves a copy of all stored notifications.
     * @return A new List containing all current notifications.
     */
    public static List<String> getAllNotifications() {
        return new ArrayList<>(allNotifications); // Return a copy to prevent external modification
    }

    /**
     * Clears all stored notifications.
     * This might be useful for "mark all as read" functionality or system resets.
     */
    public static void clearAllNotifications() {
        allNotifications.clear();
        System.out.println("NOTIFICATION MANAGER: All notifications cleared.");
    }
}
