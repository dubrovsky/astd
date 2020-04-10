package com.isc.astd.web.errors;

import com.isc.astd.web.commons.Response;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author p.dzeviarylin
 */
@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(EcpException.class)
    public ResponseEntity<Response<?>> handleEcpException(EcpException ex) {
        return ResponseEntity.badRequest().body(new Response<>(ex.getMessage(), false, true));
    }

    @ExceptionHandler(BindException.class)
    protected ResponseEntity<Response<?>> handleBindException(BindException ex) {
        final BindingResult bindingResult = ex.getBindingResult();
        final List<String> errors = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        return ResponseEntity.badRequest().body(new Response<>(errors, false));
    }

    @ExceptionHandler(Throwable.class)
    protected ResponseEntity<Response<?>> handleExceptions(Throwable ex) {
        return ResponseEntity.badRequest().body(new Response<>(ExceptionUtils.getRootCauseMessage(ex), false));
    }
}
