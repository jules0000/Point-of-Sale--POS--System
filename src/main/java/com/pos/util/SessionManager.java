package com.pos.util;

import com.pos.model.User;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Manages user sessions and authentication state.
 */
public class SessionManager {
    private static final Logger LOGGER = Logger.getLogger(SessionManager.class.getName());
    private static final ConfigLoader config = ConfigLoader.getInstance();
    private static final int SESSION_TIMEOUT_MINUTES = config.getIntProperty("security.session.timeout", 30);
    private static SessionManager instance;

    private final Map<String, SessionInfo> sessions;
    private final Map<String, Integer> loginAttempts;
    private final Map<String, LocalDateTime> lockoutTimes;

    private SessionManager() {
        this.sessions = new ConcurrentHashMap<>();
        this.loginAttempts = new ConcurrentHashMap<>();
        this.lockoutTimes = new ConcurrentHashMap<>();
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Create a new session for a user
     * @param user The user to create a session for
     * @param ipAddress The IP address of the user
     * @return The session token
     */
    public String createSession(User user, String ipAddress) {
        String token = generateSessionToken();
        SessionInfo session = new SessionInfo(user, ipAddress);
        sessions.put(token, session);
        
        LOGGER.info(String.format("Session created for user %s from %s", user.getUsername(), ipAddress));
        return token;
    }

    /**
     * Get the user associated with a session token
     * @param token The session token
     * @return The user, or null if the session is invalid
     */
    public User getUser(String token) {
        SessionInfo session = sessions.get(token);
        if (session != null && !isSessionExpired(session)) {
            session.lastAccessed = LocalDateTime.now();
            return session.user;
        }
        return null;
    }

    /**
     * Validate a session token
     * @param token The session token
     * @return true if the session is valid, false otherwise
     */
    public boolean isValidSession(String token) {
        SessionInfo session = sessions.get(token);
        if (session != null && !isSessionExpired(session)) {
            session.lastAccessed = LocalDateTime.now();
            return true;
        }
        if (session != null) {
            invalidateSession(token);
        }
        return false;
    }

    /**
     * Invalidate a session
     * @param token The session token
     */
    public void invalidateSession(String token) {
        SessionInfo session = sessions.remove(token);
        if (session != null) {
            LOGGER.info(String.format("Session invalidated for user %s", session.user.getUsername()));
        }
    }

    /**
     * Record a failed login attempt
     * @param username The username that failed to log in
     * @param ipAddress The IP address of the attempt
     * @return true if the account is now locked, false otherwise
     */
    public boolean recordFailedLogin(String username, String ipAddress) {
        int maxAttempts = config.getIntProperty("security.max.login.attempts", 3);
        int lockoutMinutes = config.getIntProperty("security.lockout.duration", 15);

        int attempts = loginAttempts.getOrDefault(username, 0) + 1;
        loginAttempts.put(username, attempts);

        if (attempts >= maxAttempts) {
            lockoutTimes.put(username, LocalDateTime.now().plusMinutes(lockoutMinutes));
            LOGGER.warning(String.format("Account %s locked due to %d failed login attempts from %s",
                    username, attempts, ipAddress));
            return true;
        }

        return false;
    }

    /**
     * Check if an account is locked
     * @param username The username to check
     * @return true if the account is locked, false otherwise
     */
    public boolean isAccountLocked(String username) {
        LocalDateTime lockoutTime = lockoutTimes.get(username);
        if (lockoutTime != null) {
            if (LocalDateTime.now().isBefore(lockoutTime)) {
                return true;
            }
            // Lockout period has expired
            lockoutTimes.remove(username);
            loginAttempts.remove(username);
        }
        return false;
    }

    /**
     * Reset failed login attempts for a user
     * @param username The username to reset
     */
    public void resetFailedLogins(String username) {
        loginAttempts.remove(username);
        lockoutTimes.remove(username);
    }

    /**
     * Get the remaining lockout time in minutes
     * @param username The username to check
     * @return The number of minutes remaining in the lockout, or 0 if not locked
     */
    public long getLockoutTimeRemaining(String username) {
        LocalDateTime lockoutTime = lockoutTimes.get(username);
        if (lockoutTime != null && LocalDateTime.now().isBefore(lockoutTime)) {
            return java.time.Duration.between(LocalDateTime.now(), lockoutTime).toMinutes();
        }
        return 0;
    }

    /**
     * Clean up expired sessions
     */
    public void cleanupExpiredSessions() {
        sessions.entrySet().removeIf(entry -> isSessionExpired(entry.getValue()));
    }

    private boolean isSessionExpired(SessionInfo session) {
        return session.lastAccessed
                .plusMinutes(SESSION_TIMEOUT_MINUTES)
                .isBefore(LocalDateTime.now());
    }

    private String generateSessionToken() {
        return java.util.UUID.randomUUID().toString();
    }

    /**
     * Inner class to hold session information
     */
    private static class SessionInfo {
        final User user;
        final String ipAddress;
        LocalDateTime lastAccessed;

        SessionInfo(User user, String ipAddress) {
            this.user = user;
            this.ipAddress = ipAddress;
            this.lastAccessed = LocalDateTime.now();
        }
    }
} 