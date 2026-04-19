package com.example.banque.exception;

/**
 * Exception personnalisée pour les erreurs métier de la banque
 */
public class BanqueException extends RuntimeException {
    
    private String errorCode;
    private String details;

    public BanqueException(String message) {
        super(message);
        this.errorCode = "BANQUE_ERROR";
    }

    public BanqueException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BanqueException(String message, String errorCode, String details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }

    public BanqueException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BANQUE_ERROR";
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getDetails() {
        return details;
    }
}
