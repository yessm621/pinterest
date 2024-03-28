package com.pinterest.error;

public class PinterestException extends RuntimeException {

    PinterestException() {

    }

    public PinterestException(String message) {
        super(message);
    }
}
