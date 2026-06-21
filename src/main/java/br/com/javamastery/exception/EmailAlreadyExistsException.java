package br.com.javamastery.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String emailAddress) {
        super("Email : " + emailAddress + " already exists.");
    }
}
