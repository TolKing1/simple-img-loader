package org.topimg.tolking.simpleimgloader.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.InvalidMimeTypeException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.topimg.tolking.simpleimgloader.controllers.rest.ImageController;

import javax.naming.InvalidNameException;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.time.LocalDateTime;

@ControllerAdvice(assignableTypes = ImageController.class)
public class ApiExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiError> handleException(IOException exception, HttpServletRequest request) {
        ApiError apiError = getApiError(
                exception,
                request,
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
        );

        return new ResponseEntity<>(apiError,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler( {InvalidNameException.class, FileSystemException.class, InvalidMimeTypeException.class})
    public ResponseEntity<ApiError> handleNameException(Exception exception, HttpServletRequest request) {
        ApiError apiError = getApiError(
                exception,
                request,
                HttpStatus.BAD_REQUEST.getReasonPhrase()
        );

        return new ResponseEntity<>(apiError,HttpStatus.BAD_REQUEST);
    }

    private static ApiError getApiError(Exception exception, HttpServletRequest request, String status) {
        logger.warn(exception.getMessage());
        return new ApiError(
                request.getRequestURI(),
                exception.getLocalizedMessage(),
                status,
                LocalDateTime.now()
        );
    }


}
