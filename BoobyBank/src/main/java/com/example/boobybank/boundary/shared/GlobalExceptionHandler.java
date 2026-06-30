package com.example.boobybank.boundary.shared;

import com.example.boobybank.control.shared.NotFoundException;
import com.example.boobybank.control.shared.UnknownCustomerException;
import jakarta.validation.ConstraintViolationException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_CONTENT;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleNotFoundException(NotFoundException ex) {
        ProblemDetail result = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        result.setTitle("Not Found");
        result.setDetail(ex.getMessage());

        return result;
    }


    @ExceptionHandler(UnknownCustomerException.class)
    @ResponseStatus(UNPROCESSABLE_CONTENT)
    public ProblemDetail handleNotFoundException(UnknownCustomerException ex) {
        ProblemDetail result = ProblemDetail.forStatus(UNPROCESSABLE_CONTENT);
        result.setTitle("Unknown Customer");
        result.setDetail(ex.getMessage());

        return result;
    }


    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(UNPROCESSABLE_CONTENT)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException exception) {
        var result = ProblemDetail.forStatus(UNPROCESSABLE_CONTENT);
        result.setTitle("Unprocessable Content");
        result.setDetail("Unable to process request due to constraint violations");
        result.setProperty(
                "violations",
                exception.getConstraintViolations()
                        .stream()
                        .map(v -> ConstraintViolationDTO.builder()
                                .field(v.getPropertyPath().toString())
                                .message(v.getMessage())
                                .build()
                        )
                        .toList());

        return result;
    }


    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        ex.getBody().setStatus(UNPROCESSABLE_CONTENT);
        ex.getBody().setProperty("violations", getViolations(ex));

        return super.handleMethodArgumentNotValid(
                ex,
                headers,
                UNPROCESSABLE_CONTENT,
                request
        );
    }


    private static @NonNull List<Object> getViolations(@NonNull MethodArgumentNotValidException ex) {
        return ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(
                        v -> ConstraintViolationDTO.builder()
                                .field(v.getField())
                                .constraint(v.getCode())
                                .message(v.getDefaultMessage())
                                .build()
                )
                .collect(Collectors.toList());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
        ProblemDetail result = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        result.setTitle("Bad Request");
        result.setDetail("Invalid request parameter: " + ex.getMessage());

        return result;
    }

}