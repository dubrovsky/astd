package com.isc.astd.web.errors;

import com.isc.astd.web.commons.Response;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * @author p.dzeviarylin
 */
@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(EcpException.class)
    public ResponseEntity<Response> handleEcpException(EcpException ex) {
        return ResponseEntity.ok().body(new Response(ex.getMessage(), false));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Response> handleExceptions(Exception ex, WebRequest request) {
        return ResponseEntity.badRequest().body(new Response(ExceptionUtils.getRootCauseMessage(ex), false));
    }
}
