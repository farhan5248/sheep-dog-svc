package org.farhan.mbt.exception;


public class MessageConsumingException extends RuntimeException {
    
    public MessageConsumingException(String message) {
        super(message);
    }
    
    public MessageConsumingException(String message, Throwable cause) {
        super(message, cause);
    }
}