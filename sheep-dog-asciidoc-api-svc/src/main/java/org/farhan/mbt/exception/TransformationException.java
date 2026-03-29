package org.farhan.mbt.exception;

public class TransformationException extends RuntimeException {

    public TransformationException(String message) {
        super("Error transforming object: " + message);
    }

    public TransformationException(String message, Throwable cause) {
        super("Error transforming object: " + message, cause);
    }
}