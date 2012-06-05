package at.ac.tuwien.esse.itseclarge.lab1;

import java.math.BigDecimal;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONException;
import org.json.JSONObject;

public class CardResponse {

	/**
	 * Erzeugt eine JSON-Response aus einem String, der JSON enthält.
	 * 
	 * @param json JSON-String
	 * @return HTTP 200 OK Antwort mit JSON als Body
	 */
	public static Response create(String json) {
		return Response.ok(CardResponse.createJSON(json)).type("application/json").build();
	}

	/**
	 * Erzeugt ein JSON-Objekt.
	 * 
	 * @param json JSON-String
	 * @return JSON-Objekt
	 */
	protected static JSONObject createJSON(String json) {
		try {
			return new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Erzeugt eine 400 Bad Request Antwort.
	 * 
	 * @param message Fehlermeldung
	 * @return HTTP 400 Bad Request mit Error-JSON-Objekt
	 */
	public static Response clientError(String message) {
		System.out.println("SENDING JSON RESPONSE: " + CardResponse.createJSON("{\"error\":\"" + message + "\"}"));
		return Response.status(Status.BAD_REQUEST).type("application/json")
				.entity(CardResponse.createJSON("{\"error\":\"" + message + "\"}")).build();
	}

	/**
	 * Erzeugt eine Antwort mit nur einem booleschen Parameter.
	 * 
	 * @param b boolescher Wert
	 * @return HTTP 200 OK Antwort
	 */
	public static Response single(boolean b) {
		System.out.println("SENDING JSON RESPONSE: " + CardResponse.createJSON("{\"result\":\"" + (b ? "true" : "false") + "\"}"));
		return CardResponse.create("{\"result\":" + (b ? "true" : "false") + "}");
	}

	/**
	 * Erzeugt eine Antwort mit nur einem BigDecimal Parameter.
	 * 
	 * @param b BigDecimal Wert
	 * @return HTTP 200 OK Antwort
	 */
	public static Response single(BigDecimal b) {
		System.out.println("SENDING JSON RESPONSE: " + CardResponse.createJSON("{\"result\":\"" + b.toString() + "\"}"));
		return CardResponse.create("{\"result\":" + b.toString() + "}");
	}

}
