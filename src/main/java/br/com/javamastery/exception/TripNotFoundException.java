package br.com.javamastery.exception;

public class TripNotFoundException extends RuntimeException{
    public TripNotFoundException(String code){
        super("Trip not found with code " + code);
    }
}
