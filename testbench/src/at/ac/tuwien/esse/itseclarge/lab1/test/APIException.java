package at.ac.tuwien.esse.itseclarge.lab1.test;

public class APIException extends Exception {

	private String message;
	
	public APIException(String string) {
		this.message = string;
	}
	
	@Override
	public String getMessage() {
		return message;
	}

	private static final long serialVersionUID = -6120398058999285328L;

}
