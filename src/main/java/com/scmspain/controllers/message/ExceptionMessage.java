package com.scmspain.controllers.message;

public class ExceptionMessage {

    private String message;
    private String exceptionClass;

    public ExceptionMessage() {}

    public ExceptionMessage(Exception ex) {
        message = ex.getMessage();
        exceptionClass = ex.getClass().getSimpleName();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public void setExceptionClass(String exceptionClass) {
        this.exceptionClass = exceptionClass;
    }
}
