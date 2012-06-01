package at.ac.tuwien.esse.itseclarge.lab1;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Verschiedene Funktionen, die das Arbeiten mit Kartendetails ermöglichen.
 * 
 * @author stephan
 */
public class Card {

	private String cardno, validity, signature;
	private BigDecimal limit;
	private long customer;
	

	public Card() {
	}

	public Card(String cardno, String validity, BigDecimal limit, long customer, String signature) {
		this.setCardno(cardno);
		this.setCustomer(customer);
		this.setLimit(limit);
		this.setSignature(signature);
		this.setValidity(validity);
	}

	public Card(String cardno, String validity) {
		this(cardno, validity, null, 0L, null);
	}

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
	public void setCustomer(long customer) {
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

	/**
	 * True wenn die Kartennummer und das Gültigkeitsdatum formal valide sind.
	 * Das hat nichts mit Gültigkeit der Karte ansich zu tun, sondern nur mit der synaktischen
	 * Korrektheit der Eingabedaten.
	 * 
	 * @param cardno Kartennummer; kann null sein
	 * @param validity Gültigkeitsdatum; kann null sein
	 * @return true wenn korrekt, sonst false
	 */
	public boolean isFormallyValid() {
			
		if(cardno == null || validity == null) return false;
		
		/*
		 * simpelste Annahme: CC Nummer 16 Digits ohne Leerzeichen, Gültigkeitsdatum Format MM/YY
		 */
		return cardno.matches("\\d{16}") && validity.matches("[0,1]/\\d{2}");
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
		return true;
	}

}
