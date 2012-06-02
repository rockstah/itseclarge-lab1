package at.ac.tuwien.esse.itseclarge.lab1.test;

import java.io.IOException;

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
import org.restlet.resource.ResourceException;
import org.restlet.util.Series;

public class KundenverwaltungClient {
	
	private static final String ENDPOINT = "https://localhost:8182/card";
	private static final String KEYSTORE_PATH = "keystore/kunden/keystore.jks";
	private static final String TRUSTSTORE_PATH = "keystore/truststore.jks";
	private static final String STORE_PASSWORD = "itsec1";
	
	protected Client client;
	
	public KundenverwaltungClient() throws Exception {
		Component comp = new Component();
		client = comp.getClients().add(Protocol.HTTPS);

		Series<Parameter> p = client.getContext().getParameters();

		// im Keystore werden die entsprechenden Zertifikate aufbewahrt
		p.add("keystorePath", KEYSTORE_PATH);
		p.add("keystorePassword", STORE_PASSWORD);
		p.add("keyPassword", "");
		
		p.add("truststorePath", TRUSTSTORE_PATH);
		p.add("truststorePassword", STORE_PASSWORD);
	}
	
	public boolean isValid(String cardno, String validity) throws Exception {
		Reference ref = new Reference(ENDPOINT + "/isValid");
		ref.addQueryParameter("cardno", cardno);
		ref.addQueryParameter("validity", validity);
		
		Request request = new Request(Method.GET, ref);
		Response response = client.handle(request);
		
		JSONObject result = new JSONObject(response.getEntity().getText());
		return result.getBoolean("result");
	}
	
	public static void main(String[] args) throws Exception {
		KundenverwaltungClient c = new KundenverwaltungClient();
		System.out.println(c.isValid("0000000000000000", "01/19"));
		System.exit(0);
	}
	
}
