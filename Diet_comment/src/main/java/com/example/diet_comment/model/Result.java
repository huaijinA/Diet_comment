package com.example.diet_comment.model;




public class Result {
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
		return new Result(1, "Success", null);
	}


	public static Result success(Object data) {
		return new Result(1, "Success", data);
	}


	public static Result error(String message) {
		return new Result(0, message, null);
	}



    public int  getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
	
}




