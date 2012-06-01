package at.ac.tuwien.esse.itseclarge.lab1;

import java.math.BigDecimal;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONException;
import org.json.JSONObject;

public class CardResponse {

	public static Response create(String json) {
		return Response.ok(CardResponse.createJSON(json)).build();
	}

	protected static JSONObject createJSON(String json) {
		try {
			return new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Response clientError(String message) {
		return Response.status(Status.BAD_REQUEST)
				.entity(CardResponse.createJSON("{\"error\":\"" + message + "\"}")).build();
	}

	public static Response single(boolean b) {
		return CardResponse.create("{\"result\":" + (b ? "true" : "false") + "}");
	}

	public static Response single(BigDecimal b) {
		return CardResponse.create("{\"result\":" + b.toString() + "}");
	}

}
