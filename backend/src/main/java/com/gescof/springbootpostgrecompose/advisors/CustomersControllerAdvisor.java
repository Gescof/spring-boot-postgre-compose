package com.gescof.springbootpostgrecompose.advisors;

import com.gescof.springbootpostgrecompose.exceptions.CustomersNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@RestControllerAdvice
public class CustomersControllerAdvisor {
    private final Clock clock;

    @ExceptionHandler(CustomersNotFoundException.class)
    public ResponseEntity<Object> handleCustomersNotFoundException(
            CustomersNotFoundException exception, WebRequest request) {
        log.warn(String.format("Handling customers not found exception for request %s [Input query params: %s]",
                Objects.nonNull(request) ? request.getDescription(true) : "",
                getQueryParamsMapString(Objects.nonNull(request) ? request.getParameterMap() : new HashMap<>())));
        return getNotFoundResponseEntity(exception.getMessage(), exception.getMessage());
    }

    private String getQueryParamsMapString(Map<String, String[]> queryParamsMap) {
        return queryParamsMap.keySet().stream()
                .map(key -> key + "=" + Arrays.toString(queryParamsMap.get(key)))
                .collect(Collectors.joining(", ", "{", "}"));
    }

    private ResponseEntity<Object> getNotFoundResponseEntity(String message, String errors) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now(clock));
        body.put("status", HttpStatus.NOT_FOUND.toString());
        body.put("message", message);
        body.put("errors", errors);
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
}
