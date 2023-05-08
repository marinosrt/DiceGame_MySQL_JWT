package S05T2N1RoyoTerolMarina.model.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Arrays;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorMessage exceptionHandler(Exception e) {
        return new ErrorMessage(e.getMessage() + " " + Arrays.toString(e.getStackTrace()));
    }

    @Data
    @AllArgsConstructor
    class ErrorMessage {
        String message;
    }
}
