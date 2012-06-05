package at.ac.tuwien.esse.itseclarge.lab1;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.engine.util.Base64;

/**
 * Verschiedene Funktionen, die das Arbeiten mit Kartendetails ermöglichen.
 * 
 * @author stephan
 */
public class Card {

	private String cardno, validity, signature;
	private BigDecimal limit;
	private Long customer;

	/**
	 * Constructor.
	 * 
	 * @param cardno Kartennummer
	 * @param validity Gültigkeitsdatum
	 * @param limit Limit
	 * @param customer Kunde
	 * @param signature digitale Signatur
	 */
	public Card(String cardno, String validity, BigDecimal limit, Long customer, String signature) {
		this.setCardno(cardno);
		this.setCustomer(customer);
		this.setLimit(limit);
		this.setSignature(signature);
		this.setValidity(validity);
	}

	/**
	 * Constructor.
	 * Erzeugt ein Card-Objekt aus einer JSON-Serialisierung.
	 * 
	 * @param o JSONObject
	 * @throws JSONException wenn ein oder mehrere Felder nicht gesetzt sind
	 */
	public Card(JSONObject o) throws JSONException {
		this.setCardno(o.getString("cardno"));
		this.setCustomer(o.getLong("customer"));
		this.setLimit(new BigDecimal(o.getDouble("limit")));
		this.setSignature(o.getString("signature"));
		this.setValidity(o.getString("validity"));
	}

	/**
	 * @return the cardno
	 */
	public String getCardno() {
		return cardno;
	}

	/**
	 * @param cardno the cardno to set
	 */
	public void setCardno(String cardno) {
		this.cardno = cardno;
	}

	/**
	 * @return the validity
	 */
	public String getValidity() {
		return validity;
	}

	/**
	 * @param validity the validity to set
	 */
	public void setValidity(String validity) {
		this.validity = validity;
	}

	/**
	 * @return the signature
	 */
	public String getSignature() {
		return signature;
	}

	/**
	 * @param signature the signature to set
	 */
	public void setSignature(String signature) {
		this.signature = signature;
	}

	/**
	 * @return the limit
	 */
	public BigDecimal getLimit() {
		return limit;
	}

	/**
	 * @param limit the limit to set
	 */
	public void setLimit(BigDecimal limit) {
		this.limit = limit;
	}

	/**
	 * @return the customer
	 */
	public long getCustomer() {
		return customer;
	}

	/**
	 * @param customer the customer to set
	 */
	public void setCustomer(Long customer) {
		this.customer = customer;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cardno == null) ? 0 : cardno.hashCode());
		result = prime * result + ((validity == null) ? 0 : validity.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Card other = (Card) obj;
		if (cardno == null) {
			if (other.cardno != null)
				return false;
		} else if (!cardno.equals(other.cardno))
			return false;
		if (validity == null) {
			if (other.validity != null)
				return false;
		} else if (!validity.equals(other.validity))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("Card { %s, %s, %s, %s, %s }", p(this.cardno), p(this.validity),
				p(this.limit), p(this.customer), p(this.signature));
	}
	
	private static String p(Object o) {
		return o == null ? "null" : o.toString();
	}

	/**
	 * True wenn alle Parameter definiert sind und Kartennummer und Gültigkeitsdatum
	 * den formalen Kriterien entsprechen. Nummern sind 16 Ziffern lang, das Gültigkeitsdatum
	 * muss im Format MM/YY spezifiziert sein.
	 * 
	 * @param cardno Kartennummer; kann null sein
	 * @param validity Gültigkeitsdatum; kann null sein
	 * @return true wenn korrekt, sonst false
	 */
	public boolean isFormallyValid() {

		// Alle Felder sind verpflichtend
		if (cardno == null || validity == null || signature == null || customer == null || limit == null)
			return false;

		// simpelste Annahme: CC Nummer 16 Digits ohne Leerzeichen, Gültigkeitsdatum Format MM/YY
		return cardno.matches("\\d{16}") && validity.matches("[0-1]\\d/\\d{2}");
	}

	/**
	 * True wenn die Karte gültig ist. Eine Karte ist Eine Karte ist genau dann gültig, wenn in der
	 * Datenbank genau ein Treffer für die übergebenen Parameter vorhanden ist, das Gültigkeitsdatum
	 * in der Zukunft liegt, die Karte einem Kunden zugeordnet ist und die digitale Signatur korrekt
	 * ist.
	 * 
	 * @param cardno Kartennummer
	 * @param validity Gültigkeitsdatum
	 * @return JSONObject mit true wenn gültig sonst false
	 */
	public boolean isValid() {
		
		//
		// Datumsprüfung
		//
		
		Calendar now = Calendar.getInstance();
		int month = now.get(Calendar.MONTH) + 1;
		int year = now.get(Calendar.YEAR);
		
		String[] parts = this.validity.split("/");
		
		// Gültigkeitsjahr ist bereits vorbei
		if (Integer.valueOf("20" + parts[1]) < year) return false;
		
		// Gültigkeitsmonat ist vorbei
		if (Integer.valueOf("20" + parts[1]) == year && Integer.valueOf(parts[0]) < month) return false;
		
		//
		// Signaturprüfung
		//
		
		String data = this.cardno + this.validity + this.limit.toString() + this.customer.toString();
		
		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream("keystore/karten/staffkey.pub"));
			byte[] key = new byte[in.available()];
			in.read(key);
			
			Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(key);
			KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
			PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
			
			dsa.initVerify(pubKey);
			dsa.update(data.getBytes("UTF-8"));
			return dsa.verify(Base64.decode(this.signature));
		
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
			
		return false;
	}

}
