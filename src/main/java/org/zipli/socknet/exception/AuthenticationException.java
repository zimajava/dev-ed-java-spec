package org.zipli.socknet.exception;

public class AuthenticationException extends RuntimeException {

    @Override
    public String getMessage(){
        return "Cannot set user authentication";
    }

    @Override
    public String toString(){
        return "AuthenticationException";
    }
}
