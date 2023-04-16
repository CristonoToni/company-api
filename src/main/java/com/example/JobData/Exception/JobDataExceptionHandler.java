package com.example.JobData.Exception;

import com.example.JobData.Model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.logging.Logger;

@ControllerAdvice
public class JobDataExceptionHandler {
    private final Logger logger = Logger.getLogger(getClass().getName());

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        String message = "An error occurred: " + e.getMessage();
        logger.severe(message);
        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(JobDataException.class)
    public ResponseEntity<ErrorResponse> handleJobDataException(JobDataException e) {
        String message = "Found Job Data error: " +  e.getMessage();
        logger.severe(message);
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
