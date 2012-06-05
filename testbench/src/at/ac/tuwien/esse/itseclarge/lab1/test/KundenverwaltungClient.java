package at.ac.tuwien.esse.itseclarge.lab1.test;

import java.io.BufferedInputStream;
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
import java.security.spec.PKCS8EncodedKeySpec;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.engine.util.Base64;
import org.restlet.ext.json.JsonRepresentation;

public class KundenverwaltungClient extends CardClient {
	
	public KundenverwaltungClient() {
		super("keystore/kunden/keystore.jks");
	}
	
	public boolean create(String cardno, String validity, BigDecimal limit, Long customer) throws APIException, JSONException, IOException {
		Reference ref = new Reference(ENDPOINT);
		
		JSONObject card = new JSONObject();
		card.put("cardno", cardno);
		card.put("validity", validity);
		card.put("limit", limit);
		card.put("customer", customer);
		card.put("signature", sign(cardno + validity + limit.toString() + customer.toString()));
		
		JSONObject result = makeRequest(Method.POST, ref, new JsonRepresentation(card));
		
		if (result.has("result")) {
			return result.getBoolean("result");
		} else {
			throw new APIException(result.getString("error"));
		}
		
	}
	
	private String sign(String data) {
		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream("keystore/kunden/staffkey.priv"));
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

	public boolean delete(String cardno, String validity) throws JSONException, IOException {
		Reference ref = new Reference(ENDPOINT);
		ref.addQueryParameter("cardno", cardno);
		ref.addQueryParameter("validity", validity);
		
		JSONObject result = makeRequest(Method.DELETE, ref);
		return result.getBoolean("result");
	}
	
}
