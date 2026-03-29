package org.farhan.mbt.exception;

public class PublishingException extends RuntimeException {

    public PublishingException(String message) {
        super("Error publishing object: " + message);
    }

    public PublishingException(String message, Throwable cause) {
        super("Error publishing object: " + message, cause);
    }
}