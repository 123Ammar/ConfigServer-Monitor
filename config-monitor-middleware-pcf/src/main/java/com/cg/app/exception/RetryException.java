package com.cg.app.exception;

public class RetryException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RetryException(String message){
		super(message);
	}
	
	
}
