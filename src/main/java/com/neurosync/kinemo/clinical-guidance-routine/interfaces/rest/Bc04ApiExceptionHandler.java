package com.neurosync.kinemo.clinicalguidanceroutine.interfaces.rest;
import com.neurosync.kinemo.clinicalguidanceroutine.application.UseCaseException;
import com.neurosync.kinemo.clinicalguidanceroutine.domain.model.DomainRuleViolation;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
/** Context-specific handling; the BC03 ApiExceptionHandler remains untouched. */
@RestControllerAdvice(basePackages="com.neurosync.kinemo.clinicalguidanceroutine.interfaces.rest")
public class Bc04ApiExceptionHandler {
    public record ErrorResponse(String code,String message,Instant timestamp){}
    @ExceptionHandler(UseCaseException.class)
    public ResponseEntity<ErrorResponse> useCase(UseCaseException ex){
        HttpStatus status=switch(ex.code()){
            case "ACCESS_DENIED"->HttpStatus.FORBIDDEN;
            case "ROUTINE_NOT_FOUND","GUIDELINE_NOT_FOUND","GUIDE_NOT_FOUND","ASSET_NOT_FOUND"->HttpStatus.NOT_FOUND;
            case "STORAGE_NOT_CONFIGURED"->HttpStatus.SERVICE_UNAVAILABLE;
            default->HttpStatus.BAD_REQUEST;
        };
        return response(status,ex.code(),ex.getMessage());
    }
    @ExceptionHandler(DomainRuleViolation.class)
    public ResponseEntity<ErrorResponse> domain(DomainRuleViolation ex){
        return response(HttpStatus.CONFLICT,"DOMAIN_RULE_VIOLATION",ex.getMessage());
    }
    private ResponseEntity<ErrorResponse> response(HttpStatus status,String code,String msg){
        return ResponseEntity.status(status).body(new ErrorResponse(code,msg,Instant.now()));
    }
}
