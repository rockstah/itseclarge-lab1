package at.ac.tuwien.esse.itseclarge.lab1.test;

import org.restlet.data.Status;

public class APIException extends Exception {

	private String message;
	private Status responseCode;
	
	public APIException(String string, Status responseCode) {
		this.message = string;
		this.responseCode = responseCode;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
	
	/**
	 * @return den HTTP-Statuscode der Antwort
	 */
	public Status getStatus() {
		return responseCode;
	}

	private static final long serialVersionUID = -6120398058999285328L;

}
