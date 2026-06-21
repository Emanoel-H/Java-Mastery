package br.com.javamastery.exception;

import java.math.BigDecimal;

public class InvalidPriceException extends RuntimeException {
    public InvalidPriceException(BigDecimal price) {
        super("Invalid price: " + price + "! The price must be higher than 0.");
    }
}
