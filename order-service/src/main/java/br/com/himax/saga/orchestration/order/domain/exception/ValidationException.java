package br.com.himax.saga.orchestration.order.domain.exception;

public class ValidationException extends RuntimeException{

    public ValidationException(String message){
        super(message);
    }
}
