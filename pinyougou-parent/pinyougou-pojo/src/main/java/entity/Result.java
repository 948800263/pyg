package entity;

import java.io.Serializable;

public class Result implements Serializable{
	
	private Boolean success;
	private String message;
	
	public Result() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	public Result(Boolean success, String message) {
		super();
		this.success = success;
		this.message = message;
	}



	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	

}
