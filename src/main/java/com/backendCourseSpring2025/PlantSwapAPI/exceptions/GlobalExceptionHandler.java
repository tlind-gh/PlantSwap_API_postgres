package com.backendCourseSpring2025.PlantSwapAPI.exceptions;

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

    //error handling for @RequestBody failing @Valid check
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationError(MethodArgumentNotValidException exception) {
        String errorMessages = "Invalid input argument(s):";

        //extract the part of the error message that correlates with the "message" string set in the annotation in the class
        for (ObjectError objectError : exception.getAllErrors()) {
            String[] errorFields = objectError.toString().split(";");
            String errorMessage = errorFields[errorFields.length-1].substring(18).replace("]","");
            errorMessages = errorMessages.concat("\n- "+errorMessage);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
    }

    //error handling for attempting to delete an object that cannot be deleted (i.e., a plant with pending transactions)
    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<String> handleUnsupportedObjectDeletion(UnsupportedOperationException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    //error handling for failing checks of input arguments for create and update methods in Service classes
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input argument(s):\n -"+exception.getMessage());
    }

    //error handling for when path variable id does not correspond to any object in the relevant database table
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElement(NoSuchElementException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    /*handles other errors, including trying to save non-unique string to a field of database with requirement unique
    not spring, but hibernate error, since it is a database constraint and not an annotation*/
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception exception) {
        String ErrorMessage;
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

