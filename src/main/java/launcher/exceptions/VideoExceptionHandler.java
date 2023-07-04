package launcher.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.logging.Logger;

@ControllerAdvice
public class VideoExceptionHandler {
    private final Logger LOGGER = Logger.getLogger(VideoExceptionHandler.class.getName());

    @ExceptionHandler(WrongVideoException.class)
    public ResponseEntity<String> handleWrongVideoException(WrongVideoException e) {
        LOGGER.info(e.getMessage());
        e.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
    }


}