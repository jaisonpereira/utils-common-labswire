package com.wirelabs.exceptions;

/**
 * @author jpereira Exception for business rules
 */
public class LabsValidationException extends Exception {

	private static final long serialVersionUID = -6943081794946829463L;

	public LabsValidationException(String msg) {
		super(msg);
	}

	public LabsValidationException(String msg, Throwable e) {
		super(msg, e);
	}

}
