package com.pos.utils;

import com.pos.model.User;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    private LocalDateTime lastActivity;
    private Timer sessionTimer;
    private Consumer<Void> onSessionExpired;
    private final int sessionTimeoutMinutes;
    private Map<String, SessionInfo> sessions;
    private static final Logger LOGGER = Logger.getLogger(SessionManager.class.getName());

    private SessionManager() {
        ConfigLoader config = ConfigLoader.getInstance();
        this.sessionTimeoutMinutes = config.getSessionTimeout();
        this.sessionTimer = new Timer(true);
        sessions = new ConcurrentHashMap<>();
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void startSession(User user, Consumer<Void> onExpired) {
        this.currentUser = user;
        this.onSessionExpired = onExpired;
        updateLastActivity();
        startSessionTimer();
        
        // Log the login
        AuditLogger.logUserAction(
            user.getId(),
            "LOGIN",
            "User logged in: " + user.getUsername()
        );
    }

    public void endSession() {
        if (currentUser != null) {
            // Log the logout
            AuditLogger.logUserAction(
                currentUser.getId(),
                "LOGOUT",
                "User logged out: " + currentUser.getUsername()
            );
        }
        
        this.currentUser = null;
        this.lastActivity = null;
        this.onSessionExpired = null;
        stopSessionTimer();
    }

    public void updateLastActivity() {
        this.lastActivity = LocalDateTime.now();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isSessionActive() {
        return currentUser != null && !isSessionExpired();
    }

    public boolean isAdmin() {
        return isSessionActive() && currentUser.isAdmin();
    }

    public boolean isCashier() {
        return isSessionActive() && currentUser.isCashier();
    }

    private boolean isSessionExpired() {
        if (lastActivity == null) {
            return true;
        }
        
        LocalDateTime expirationTime = lastActivity.plusMinutes(sessionTimeoutMinutes);
        return LocalDateTime.now().isAfter(expirationTime);
    }

    private void startSessionTimer() {
        stopSessionTimer(); // Stop any existing timer
        
        sessionTimer = new Timer(true);
        sessionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isSessionExpired()) {
                    handleSessionExpired();
                }
            }
        }, 1000, 1000); // Check every second
    }

    private void stopSessionTimer() {
        if (sessionTimer != null) {
            sessionTimer.cancel();
            sessionTimer.purge();
        }
    }

    private void handleSessionExpired() {
        if (currentUser != null) {
            // Log the session expiration
            AuditLogger.logUserAction(
                currentUser.getId(),
                "SESSION_EXPIRED",
                "Session expired for user: " + currentUser.getUsername()
            );
        }
        
        // End the session
        endSession();
        
        // Notify the UI
        if (onSessionExpired != null) {
            onSessionExpired.accept(null);
        }
    }

    public void startSession(User user) {
        String sessionId = UUID.randomUUID().toString();
        SessionInfo sessionInfo = new SessionInfo(user, LocalDateTime.now());
        sessions.put(sessionId, sessionInfo);
        LOGGER.info("Started session for user: " + user.getUsername());
    }

    private static class SessionInfo {
        private final User user;
        private final LocalDateTime loginTime;
        private LocalDateTime lastActivityTime;

        public SessionInfo(User user, LocalDateTime loginTime) {
            this.user = user;
            this.loginTime = loginTime;
            this.lastActivityTime = loginTime;
        }

        public User getUser() {
            return user;
        }

        public LocalDateTime getLoginTime() {
            return loginTime;
        }

        public LocalDateTime getLastActivityTime() {
            return lastActivityTime;
        }

        public void updateLastActivityTime() {
            this.lastActivityTime = LocalDateTime.now();
        }
    }
} 
 