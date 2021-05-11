package wooteco.subway.line.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.DuplicateLineNameException;
import wooteco.subway.exception.NotAddSectionException;
import wooteco.subway.exception.NotContainStationsException;
import wooteco.subway.exception.NotExistLineException;
import wooteco.subway.exception.NotExistSectionException;
import wooteco.subway.exception.NotFoundTerminalStationException;

@ControllerAdvice
public class LineAdvice {

    @ExceptionHandler(DuplicateLineNameException.class)
    public ResponseEntity<String> handleDuplicateLineNameException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotExistLineException.class)
    public ResponseEntity<String> handleNotExistLineException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotAddSectionException.class)
    public ResponseEntity<String> handleNotAddSectionException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotContainStationsException.class)
    public ResponseEntity<String> handleNotContainStationsException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotExistSectionException.class)
    public ResponseEntity<String> handleNotExistSectionException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotFoundTerminalStationException.class)
    public ResponseEntity<String> handleNotFoundTerminalStationException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
