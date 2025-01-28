package com.backendContextAssignment1.plantSwap_postgres.exceptions;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {
    //bad request (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationError(MethodArgumentNotValidException exception) {
        String errorMessages = "Invalid input argument(s):";
        for (ObjectError objectError : exception.getAllErrors()) {
            String[] errorFields = objectError.toString().split(";");
            String errorMessage = errorFields[errorFields.length-1].substring(18).replace("]","");
            errorMessages = errorMessages.concat("\n- "+errorMessage);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<String> handleUnsupportedObjectDeletion(UnsupportedOperationException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input argument(s):\n -"+exception.getMessage());
    }

    //Not found (404)
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElement(NoSuchElementException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    //Other errors (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception exception) {
        String ErrorMessage = "";
        if (exception.getCause() instanceof ConstraintViolationException && exception.getMessage().contains("username")) {
            ErrorMessage = "Username is already registered to another user in the database (username must be unique)";
        } else if (exception.getCause() instanceof ConstraintViolationException && exception.getMessage().contains("email")) {
            ErrorMessage = "Email is already registered to another user in the database (email must be unique)";
        } else {
            ErrorMessage = "Unexpected server error (see detailed message below):\n\n" +exception.getMessage();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorMessage);
    }

}

