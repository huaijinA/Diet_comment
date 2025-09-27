package com.example.diet_comment.model;

public class Result {
	public static final int SUCCESS = 200;
	public static final int ERROR = 500;
	private int code;
	private String message;
	private Object data;
	public Result() {
	}

	public Result(int code, String message, Object data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public static Result success() {
		return new Result(SUCCESS, "Success", null);
	}


	public static Result success(Object data) {
		return new Result(SUCCESS, "Success", data);
	}


	public static Result error(String message) {
		return new Result(ERROR, message, null);
	}
}




