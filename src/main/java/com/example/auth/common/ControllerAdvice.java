package com.example.auth.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class ControllerAdvice {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handle(MethodArgumentNotValidException exception) {
        List<String> errorMessages = exception
                .getBindingResult()
                .getAllErrors()
                .stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
        return ResponseEntity.badRequest().body(errorMessages);
    }

}
