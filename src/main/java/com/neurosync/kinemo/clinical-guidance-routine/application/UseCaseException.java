package com.neurosync.kinemo.clinicalguidanceroutine.application;
public class UseCaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final String code;
    public UseCaseException(String code,String message){super(message);this.code=code;}
    public String code(){return code;}
}
