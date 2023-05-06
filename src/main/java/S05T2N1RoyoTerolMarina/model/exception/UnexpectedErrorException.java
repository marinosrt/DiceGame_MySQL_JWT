package S05T2N1RoyoTerolMarina.model.exception;

public class UnexpectedErrorException extends RuntimeException{

    private String message;

    public UnexpectedErrorException(String message) {
        super(message);
    }



}
