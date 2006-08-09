/*
 * Created on Dec 12, 2004
 */
package org.mindswap.exceptions;

/**
 * @author Evren Sirin
 */
public class ExecutionException extends RuntimeException {
    public ExecutionException() {
        super();
    }

    public ExecutionException(String message) {
        super(message);
    }

    public ExecutionException(Exception e) {
        super(e);
    }   
}
