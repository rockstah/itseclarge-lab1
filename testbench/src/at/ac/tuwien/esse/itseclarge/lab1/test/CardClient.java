package at.ac.tuwien.esse.itseclarge.lab1.test;

import java.io.IOException;
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
	
	public JSONObject makeRequest(Method method, Reference reference) throws JSONException, IOException {
		Request request = new Request(method, reference);
		Response response = client.handle(request);
		return new JSONObject(response.getEntity().getText());
	}
	
	public JSONObject makeRequest(Method method, Reference reference, Representation entity) throws JSONException, IOException {
		Request request = new Request(method, reference, entity);
		Response response = client.handle(request);
		return new JSONObject(response.getEntity().getText());
	}
	
	public boolean isValid(String cardno, String validity) throws Exception {
		Reference ref = new Reference(ENDPOINT + "/isValid");
		ref.addQueryParameter("cardno", cardno);
		ref.addQueryParameter("validity", validity);
		
		JSONObject result = makeRequest(Method.GET, ref);
		return result.getBoolean("result");
	}
	
	public BigDecimal limit(String cardno, String validity) throws Exception {
		Reference ref = new Reference(ENDPOINT + "/limit");
		ref.addQueryParameter("cardno", cardno);
		ref.addQueryParameter("validity", validity);
		
		JSONObject result = makeRequest(Method.GET, ref);
		return new BigDecimal(result.getDouble("result"));
	}
	
}
