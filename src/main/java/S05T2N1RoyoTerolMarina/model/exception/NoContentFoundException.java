package S05T2N1RoyoTerolMarina.model.exception;

public class NoContentFoundException extends RuntimeException{

    private String message;

    public NoContentFoundException(String message) {
        super(message);
    }

}
