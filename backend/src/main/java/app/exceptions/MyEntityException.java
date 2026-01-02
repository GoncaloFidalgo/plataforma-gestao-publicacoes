package app.exceptions;

public class MyEntityException extends Exception {
    private String message;

    public MyEntityException(String message) {
        this.message = message;
    }
}
