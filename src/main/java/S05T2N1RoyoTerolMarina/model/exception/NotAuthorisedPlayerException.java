package S05T2N1RoyoTerolMarina.model.exception;

public class NotAuthorisedPlayerException extends RuntimeException{

    private String message;

    public NotAuthorisedPlayerException(String message) {
        super(message);
    }
}