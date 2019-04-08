package com.wirelabs.exceptions;

public class EntityNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -3748413740495995992L;

    public EntityNotFoundException(String msg) {
        super(msg);
    }

    public EntityNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
