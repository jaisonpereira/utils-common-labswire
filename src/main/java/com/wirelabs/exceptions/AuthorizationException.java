package com.wirelabs.exceptions;

import java.io.Serializable;

public class AuthorizationException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -3440232457813115287L;

    public AuthorizationException(String msg) {
        super(msg);
    }

    public AuthorizationException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
