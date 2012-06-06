package at.ac.tuwien.esse.itseclarge.lab1.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Client;
import org.restlet.Component;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.util.Series;

public abstract class CardClient {

	public static final String ENDPOINT = "https://localhost:8182/card";

	private static final String TRUSTSTORE_PATH = "keystore/truststore.jks";
	private static final String STORE_PASSWORD = "itsec1";

	private Client client;

	
	
	
	/**
	 * Initialisiert den Client und den dazugehörigen Keystore.
	 * 
	 * @param keyStorePath Pfad zum clientspezifischen Keystore.
	 */
	public CardClient(String keyStorePath) {
		Component comp = new Component();
		client = comp.getClients().add(Protocol.HTTPS);

		Series<Parameter> p = client.getContext().getParameters();

		// im Keystore werden die entsprechenden Zertifikate aufbewahrt
		p.add("keystorePath", keyStorePath);
		p.add("keystorePassword", STORE_PASSWORD);
		p.add("keyPassword", "");

		p.add("truststorePath", TRUSTSTORE_PATH);
		p.add("truststorePassword", STORE_PASSWORD);
	}

	/**
	 * Setzt einen Request mit method auf reference ab.
	 * 
	 * @param method Request-Methode
	 * @param reference Referenz (= URI)
	 * @return JSON-Resultat
	 * @throws IOException bei Netzwerkfehlern
	 * @throws APIException bei API-Fehlern
	 */
	public JSONObject makeRequest(Method method, Reference reference) throws IOException,
			APIException {
		Request request = new Request(method, reference);
		Response response = client.handle(request);

		try {
			JSONObject result = new JSONObject(response.getEntity().getText());
			if (result.has("result")) {
				return result;
			} else {
				throw new APIException(result.getString("error"), response.getStatus());
			}
		} catch (JSONException e) {
			throw new APIException(e.getMessage(), response.getStatus());
		}
	}

	/**
	 * Setzt einen Request mit method auf reference ab und schickt entity als Body mit.
	 * 
	 * @param method Request-Methode
	 * @param reference Referenz (= URI)
	 * @param entity Request-Body
	 * @return JSON-Resultat
	 * @throws IOException bei Netzwerkfehlern
	 * @throws APIException bei API-Fehlern
	 */
	public JSONObject makeRequest(Method method, Reference reference, Representation entity)
			throws IOException, APIException {
		Request request = new Request(method, reference, entity);
		Response response = client.handle(request);

		try {
			JSONObject result = new JSONObject(response.getEntity().getText());
			if (result.has("result")) {
				return result;
			} else {
				throw new APIException(result.getString("error"), response.getStatus());
			}
		} catch (JSONException e) {
			throw new APIException(e.getMessage(), response.getStatus());
		}
	}

	/**
	 * Ruft den Status einer Karte ab.
	 * 
	 * @param cardno Kartennummer
	 * @param validity Gültigkeitsdatum
	 * @return true wenn gültig, sonst false
	 * @throws IOException bei Netzwerkfehlern
	 * @throws APIException bei API-Fehlern
	 */
	public boolean isValid(String cardno, String validity) throws IOException, APIException {
		Reference ref = new Reference(ENDPOINT + "/isValid");
		ref.addQueryParameter("cardno", cardno);
		ref.addQueryParameter("validity", validity);

		JSONObject result = makeRequest(Method.GET, ref);
		try {
			return result.getBoolean("result");
		} catch (JSONException e) {
			return false;
		}
	}

	/**
	 * Ruft das Limit einer Karte ab.
	 * 
	 * @param cardno Kartennummer
	 * @param validity Gültigkeitsdatum
	 * @return das Limit
	 * @throws APIException bei API-Fehlern
	 * @throws IOException bei Netzwerkfehlern
	 */
	public BigDecimal limit(String cardno, String validity) throws APIException, IOException {
		Reference ref = new Reference(ENDPOINT + "/limit");
		ref.addQueryParameter("cardno", cardno);
		ref.addQueryParameter("validity", validity);

		JSONObject result = makeRequest(Method.GET, ref);

		try {
			return new BigDecimal(result.getDouble("result"));
		} catch (JSONException e) {
			throw new APIException(e.getMessage(), null);
		}
	}

}
