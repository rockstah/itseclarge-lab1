package at.ac.tuwien.esse.itseclarge.lab1.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import org.json.JSONObject;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.engine.util.Base64;
import org.restlet.ext.json.JsonRepresentation;

public class KundenverwaltungClient extends CardClient {
	
	public KundenverwaltungClient() {
		super("keystore/kunden/keystore.jks");
	}
	
	public boolean create(String cardno, String validity, BigDecimal limit, Long customer) throws Exception {
		Reference ref = new Reference(ENDPOINT);
		
		JSONObject card = new JSONObject();
		card.put("cardno", cardno);
		card.put("validity", validity);
		card.put("limit", limit);
		card.put("customer", customer);
		card.put("signature", sign(cardno + validity + limit.toString() + customer.toString()));
		
		JSONObject result = makeRequest(Method.POST, ref, new JsonRepresentation(card));
		return result.getBoolean("result");
	}
	
	private String sign(String data) {
		try {
			FileInputStream f = new FileInputStream("keystore/kunden/staffkey.priv");
			byte[] key = new byte[f.available()];
			f.read(key);
			
			Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
			X509EncodedKeySpec privKeySpec = new X509EncodedKeySpec(key);
			KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
			PrivateKey privKey = keyFactory.generatePrivate(privKeySpec);
			
			dsa.initSign(privKey);
			dsa.update(data.getBytes());
			
			return Base64.encode(dsa.sign(), false);
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		
		return ""; 
	}
	
	public static void main(String[] args) throws Exception {
		KundenverwaltungClient c = new KundenverwaltungClient();
		System.out.println(c.isValid("0000000000000000", "01/19"));
		System.out.println(c.create("0000000000000000", "01/19", new BigDecimal(2000), 1L));
		System.exit(0);
	}
	
}
