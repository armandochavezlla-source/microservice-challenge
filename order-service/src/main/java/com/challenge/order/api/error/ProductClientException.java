package com.challenge.order.api.error;

public class ProductClientException extends DomainException {
    public ProductClientException(String code, String message) {
        super(code, message);
    }
}

