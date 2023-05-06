package S05T2N1RoyoTerolMarina.model.exception;

public class PlayerNotFoundException extends RuntimeException{

    private String resourceName;

    private Long idNumber;

    public PlayerNotFoundException(String resourceName, Long idNumber) {
        super(String.format("'%s not found with ID: '%s", resourceName, idNumber));
        this.resourceName = resourceName;
        this.idNumber = idNumber;
    }

    public PlayerNotFoundException(String message) {
        super(message);
    }
}
