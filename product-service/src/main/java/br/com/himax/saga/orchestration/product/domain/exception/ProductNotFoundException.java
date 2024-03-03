package br.com.himax.saga.orchestration.product.domain.exception;

public class ProductNotFoundException  extends RuntimeException{

    public ProductNotFoundException(String msg){
        super(msg);
    }
}
