package br.com.himax.saga.orchestration.inventory.domain.exception;

public class ValidationException extends RuntimeException{

    public ValidationException(String message){
        super(message);
    }
}
