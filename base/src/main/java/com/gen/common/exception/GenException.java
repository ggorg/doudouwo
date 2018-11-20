package com.gen.common.exception;


public class GenException extends Exception  {
	private Integer code;
	private Object info;
	private static final long serialVersionUID = 1L;
	public GenException(String message, Throwable t) {
		super(message, t);
	}
	public GenException(String message) {
		super(message);

	}
	public GenException(Integer code,String message) {
		super(message);
		this.code=code;
	}
	public GenException(Integer code,String message,Object info) {
		super(message);
		this.code=code;
		this.info=info;
	}
	@Override
	public String toString() {
		return "GenException{" +
				"reCode=" + code +
				"reMsg=" + this.getMessage()+
				"info=" + this.info+
				'}';
	}
}
