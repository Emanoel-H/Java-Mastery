package br.com.javamastery.exception;

public class CancellationDeadlineExceededException extends RuntimeException {
    public CancellationDeadlineExceededException() {
        super("Cancellation deadline exceeded! You can only cancel a ticket up to 1 hour before its departure.");
    }
}
