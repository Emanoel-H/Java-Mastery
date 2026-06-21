package br.com.javamastery.exception;

public class CityNotFoundException extends RuntimeException {
    public CityNotFoundException(String cityName) {
        super("The city "+ cityName +" was not found");
    }
}
