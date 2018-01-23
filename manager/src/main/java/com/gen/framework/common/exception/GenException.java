package com.gen.framework.common.exception;


public class GenException extends Exception  {

	private static final long serialVersionUID = 1L;
	public GenException(String message, Throwable t) {
		super(message, t);
	}
	public GenException(String message) {
		super(message);
	}
}
