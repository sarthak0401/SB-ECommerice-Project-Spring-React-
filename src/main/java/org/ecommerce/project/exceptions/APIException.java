package org.ecommerce.project.exceptions;

public class APIException extends RuntimeException{
    public APIException() {
    }

    public APIException(String message) {
        super(message); // THis goes to the parent class RuntimeException
    }
}
