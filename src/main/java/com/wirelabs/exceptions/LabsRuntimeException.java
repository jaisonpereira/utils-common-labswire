package com.wirelabs.exceptions;

/**
 * @author jpereira Exception for business rules
 */
public class LabsRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -3646662259617466888L;

	public LabsRuntimeException(String msg) {
		super(msg);
	}

	public LabsRuntimeException(String msg, Throwable e) {
		super(msg, e);
	}

}
