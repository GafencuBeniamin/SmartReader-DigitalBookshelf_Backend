package com.app.SmartReader.utils.exceptions;

public class CrudOperationException extends RuntimeException{
    public CrudOperationException(String message){
        super(message);
    }
}
