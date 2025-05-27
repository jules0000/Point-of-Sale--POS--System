package com.pos.utils;

import javax.swing.*;
import java.sql.SQLException;

public class ExceptionHandler {
    private static final LogManager logger = new LogManager();

    public static void handleException(Exception e, String context) {
        handleException(e, context, null);
    }

    public static void handleException(Exception e, String context, JComponent component) {
        // Log the exception
        logger.error("Exception in " + context, e);

        // Determine the user-friendly message
        String message = getUserFriendlyMessage(e, context);

        // Show error dialog
        if (component != null) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(component),
                    message,
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            });
        }

        // Log to audit trail if necessary
        if (shouldAuditLog(e)) {
            AuditLogger.logSystemAction(
                "ERROR",
                String.format("Error in %s: %s", context, e.getMessage())
            );
        }
    }

    private static String getUserFriendlyMessage(Exception e, String context) {
        if (e instanceof SQLException) {
            return handleSQLException((SQLException) e, context);
        } else if (e instanceof IllegalArgumentException) {
            return e.getMessage();
        } else if (e instanceof SecurityException) {
            return "You don't have permission to perform this action.";
        } else if (e instanceof IllegalStateException) {
            return "The system is in an invalid state. Please try again.";
        } else if (e instanceof NullPointerException) {
            return "Required data is missing. Please check your input.";
        } else {
            return String.format(
                "An unexpected error occurred while %s. Please try again or contact support.",
                context
            );
        }
    }

    private static String handleSQLException(SQLException e, String context) {
        int errorCode = e.getErrorCode();
        String sqlState = e.getSQLState();

        // Common MySQL error codes
        switch (errorCode) {
            case 1045: // Access denied
                return "Database access denied. Please check your credentials.";
            case 1049: // Unknown database
                return "Database does not exist. Please check your configuration.";
            case 1062: // Duplicate entry
                return "This record already exists.";
            case 1451: // Foreign key constraint
                return "This record cannot be deleted because it is referenced by other records.";
            case 1452: // Foreign key constraint
                return "Referenced record does not exist.";
            case 1146: // Table doesn't exist
                return "Database table not found. Please check your installation.";
            case 1213: // Deadlock
                return "Database conflict detected. Please try again.";
            case 1064: // SQL syntax error
                logger.error("SQL Syntax Error: " + e.getMessage());
                return "Invalid database query.";
            default:
                if (sqlState != null) {
                    switch (sqlState.substring(0, 2)) {
                        case "23": // Integrity constraint violation
                            return "Data integrity error. Please check your input.";
                        case "25": // Invalid transaction state
                            return "Transaction error. Please try again.";
                        case "28": // Invalid authorization
                            return "Database access denied.";
                        case "40": // Transaction rollback
                            return "Operation cancelled. Please try again.";
                        case "42": // Syntax error or access rule violation
                            return "Invalid database operation.";
                        default:
                            return "Database error occurred. Please try again.";
                    }
                }
                return "Database error occurred. Please try again.";
        }
    }

    private static boolean shouldAuditLog(Exception e) {
        return e instanceof SQLException ||
               e instanceof SecurityException ||
               e instanceof IllegalStateException;
    }

    public static void showError(String message) {
        showError(message, null);
    }

    public static void showError(String message, JComponent component) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                component != null ? SwingUtilities.getWindowAncestor(component) : null,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        });
    }

    public static void showWarning(String message) {
        showWarning(message, null);
    }

    public static void showWarning(String message, JComponent component) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                component != null ? SwingUtilities.getWindowAncestor(component) : null,
                message,
                "Warning",
                JOptionPane.WARNING_MESSAGE
            );
        });
    }

    public static boolean showConfirm(String message) {
        return showConfirm(message, null);
    }

    public static boolean showConfirm(String message, JComponent component) {
        return JOptionPane.showConfirmDialog(
            component != null ? SwingUtilities.getWindowAncestor(component) : null,
            message,
            "Confirm",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        ) == JOptionPane.YES_OPTION;
    }
} 