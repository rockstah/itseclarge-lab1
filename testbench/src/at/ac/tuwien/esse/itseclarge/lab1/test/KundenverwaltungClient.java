package at.ac.tuwien.esse.itseclarge.lab1.test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import org.eclipse.jetty.util.log.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.engine.util.Base64;
import org.restlet.ext.json.JsonRepresentation;

public class KundenverwaltungClient extends CardClient {

	
	public static void main(String[] args) throws IOException, APIException{
		
		KundenverwaltungClient client = new KundenverwaltungClient();
		InputStreamReader stdin = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(stdin);
		String input = ""; 
		
		while (!(input.equals("3"))){
				
				printMenu();			
				input = in.readLine();  
				
				if (input.equals("1")){ //erstelle Karte
					
						System.out.println("Kartennummer:");
						String cardno = in.readLine();
						
						System.out.println("Gültig bis MM/YY:");
						String validity = in.readLine();	
						
						System.out.println("Limit:");
						BigDecimal limit = new BigDecimal(in.readLine());	

						System.out.println("Kunde Nr.:");
						Long customer = new Long(in.readLine());						
						
						boolean sucess;
						
						try{
							sucess = client.create(cardno,validity, limit, customer);
						}catch (APIException e) {
							sucess = false;
							System.err.println(e.getMessage());
							continue;
						}
						
						if(sucess){
							System.out.println("Karte angelegt");
						}else{
							System.out.println("Fehler beim Anlegen der Karte");							
						}
					
					
					}else if(input.equals("2")){ //lösche Karte
						
						System.out.println("Kartennummer:");
						String cardno = in.readLine();
						
						System.out.println("Gültig bis MM/YY:");
						String validity = in.readLine();	
						
											
						boolean sucess;
						
						try{
							sucess = client.delete(cardno,validity);
						}catch (APIException e) {
							sucess = false;
							System.err.println(e.getMessage());
							continue;
						}catch(JSONException e){
							sucess = false;
							System.err.println(e.getMessage());	
							continue;
						}
						
						if(sucess){
							System.out.println("Karte angelegt");
						}else{
							System.out.println("Fehler beim Anlegen der Karte");							
						}						
						
					}
				}
		
		

		System.exit(0);
	}
	
	
	private static void printMenu(){
		
		System.out.println("Kundenverwaltung");
		System.out.println("Optionen:\n");		
		System.out.println("\t1) Erstelle Karte");			
		System.out.println("\t2) Lösche Karte");		
		System.out.println("\t3) Beenden");
		
		System.out.println("\nAuswahl:");	
		
	}
	
	/**
	 * Constructor.
	 * Initialisiert die Basisklasse {@link CardClient#CardClient(String)} mit dem korrekten Keystore.
	 */
	public KundenverwaltungClient() {
		super("keystore/kunden/keystore.jks");
	}

	/**
	 * Erzeugt eine Karte.
	 * 
	 * @param cardno Kartennummer
	 * @param validity Gültigkeitsdatum
	 * @param limit Limit
	 * @param customer Kunden-Referenz
	 * @return true wenn die Karte erzeugt wurde, sonst false
	 * @throws APIException bei API-Fehlern wie z.B. ungültigen Eingabedaten
	 * @throws IOException bei Netzwerkfehlern
	 */
	public boolean create(String cardno, String validity, BigDecimal limit, Long customer)
			throws APIException, IOException {
		Reference ref = new Reference(ENDPOINT);
		JSONObject card = new JSONObject();

		try {
			card.put("cardno", cardno);
			card.put("validity", validity);
			card.put("limit", limit);
			card.put("customer", customer);
			card.put("signature", sign(cardno + validity + limit.toString() + customer.toString()));
			return makeRequest(Method.POST, ref, new JsonRepresentation(card)).getBoolean("result");
		} catch (JSONException e) {
			Log.warn("JSON was not well-formed.");
			return false;
		}
	}

	/**
	 * Signiert die Eingabedaten und retouniert die Signatur in Base64-Kodierung.
	 * 
	 * @param data Eingabedaten
	 * @return Base64-kodierte Signatur
	 */
	private String sign(String data) {
		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(
					"keystore/kunden/staffkey.priv"));
			byte[] key = new byte[in.available()];
			in.read(key);

			Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
			PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(key);
			KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
			PrivateKey privKey = keyFactory.generatePrivate(privKeySpec);

			dsa.initSign(privKey);
			dsa.update(data.getBytes("UTF-8"));

			return Base64.encode(dsa.sign(), false);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Entfernt eine Karte.
	 * 
	 * @param cardno Kartennummer
	 * @param validity Gültigkeitsdatum
	 * @return true
	 * @throws JSONException wenn kein "result"-Feld im JSON-Objekt ist
	 * @throws IOException bei Netzwerkfehlern
	 * @throws APIException bei API-Fehlern
	 */
	public boolean delete(String cardno, String validity) throws JSONException, IOException,
			APIException {
		Reference ref = new Reference(ENDPOINT);
		ref.addQueryParameter("cardno", cardno);
		ref.addQueryParameter("validity", validity);

		JSONObject result = makeRequest(Method.DELETE, ref);
		return result.getBoolean("result");
	}

}
