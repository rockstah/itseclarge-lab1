package at.ac.tuwien.esse.itseclarge.lab1.DAO;
import at.ac.tuwien.esse.itseclarge.lab1.Card;

/**
 * 
 * @author paul
 * only some basic CRUD  
 *
 */
public interface CardDAO {

	/**
	 * Speichert eine Karte in die Datenquelle.
	 * 
	 * @param card Card-Objekt
	 */
	public void createCard(Card card);
	
	/**
	 * Löscht eine Karte aus der Datenquelle.
	 * 
	 * @param card Card-Objekt
	 */
	public void deleteCard(Card card);
	
	/**
	 * Holt eine Karte aus der Datenquelle und erzeugt ein entsprechendes Card-Objekt.
	 * 
	 * @param cardno Kartennummer
	 * @param validity Gültigkeitsdatum
	 * @return Card-Objekt oder null wenn kein passendes existiert
	 */
	public Card readCard(String cardno, String validity);
	
}
