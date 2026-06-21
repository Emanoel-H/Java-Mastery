package br.com.javamastery.exception;

public class TicketNotFoundException extends RuntimeException{
    public TicketNotFoundException(String code){
        super("Ticket not found with code " + code);
    }
}
