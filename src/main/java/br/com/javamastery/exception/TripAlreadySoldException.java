package br.com.javamastery.exception;

public class TripAlreadySoldException extends RuntimeException {
    public TripAlreadySoldException(String code) {
        super("Trip "+ code +" can't be deleted for it's already related to a sale.");
    }
}
