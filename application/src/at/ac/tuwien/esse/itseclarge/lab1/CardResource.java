package at.ac.tuwien.esse.itseclarge.lab1;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import at.ac.tuwien.esse.itseclarge.lab1.DAO.CardDAO;
import at.ac.tuwien.esse.itseclarge.lab1.DAO.InvalidCardException;
import at.ac.tuwien.esse.itseclarge.lab1.DAO.JDBC.JDBCCardDAO;

/**
 * Die Resource auf der alle Operationen spezifiziert sind.
 * 
 * @author stephan
 */
@Path("card")
public class CardResource {
	
	private CardDAO cardDAO = new JDBCCardDAO();
	
	/**
	 * Ruft das Limit für die gegebene Karte ab.
	 * 
	 * @param cardno Kartennummer
	 * @param validity Gültigkeitsdatum
	 * @return JSON mit "result" : 0000.0
	 */
	@GET
	@Path("limit")
	@Produces("application/json")
	public Response limit(@QueryParam("cardno") String cardno,
			@QueryParam("validity") String validity) {

		Card c = cardDAO.readCard(cardno, validity);

		if (c == null) {
			return CardResponse.clientError("This card does not exist. Or parameters are invalid.");
		}

		return CardResponse.single(c.getLimit());
	}

	/**
	 * Überprüft die Gültigkeit einer Karte.
	 * Eine Karte ist gültig, wenn sie existiert und ihre digitale Signatur gültig ist.
	 * 
	 * @param cardno Kartennummer
	 * @param validity Gültigkeitsdatum
	 * @return JSON-Objekt mit dem Resultat
	 */
	@GET
	@Path("isValid")
	@Produces("application/json")
	public Response isValid(@QueryParam("cardno") String cardno,
			@QueryParam("validity") String validity) {

		Card c = cardDAO.readCard(cardno, validity);
		return CardResponse.single((c == null) ? false : c.isValid());
	}
	
	/**
	 * Erzeugt eine neue Karte.
	 * 
	 * @param cardObject die neue Karte als Request-Body
	 * @return JSON-Objekt mit true/false oder einem Fehler
	 */
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response create(InputStream cardObject) {
		StringWriter w = new StringWriter();
		try {
			IOUtils.copy(cardObject, w);
		} catch (IOException e) {
			return CardResponse.clientError("Error while retrieving card object.");
		}
		
		Card c;
		try {
			c = new Card(new JSONObject(w.toString()));
		} catch (JSONException e) {
			return CardResponse.clientError("Error while parsing your JSON object.");
		}
		
		try {
			cardDAO.createCard(c);
		} catch (InvalidCardException e) {
			return CardResponse.clientError("The submitted card object was not valid.");
		}
		
		return CardResponse.single(true);
	}
	
	/**
	 * Entfernt eine Karte.
	 * 
	 * @param cardno Kartennummer
	 * @param validity Gültigkeitsdatum
	 * @return JSON-Objekt mit true
	 */
	@DELETE
	@Produces("application/json")
	public Response delete(@QueryParam("cardno") String cardno, @QueryParam("validity") String validity) {
		Card c = cardDAO.readCard(cardno, validity);
		if (c != null) cardDAO.deleteCard(c);
		return CardResponse.single(true);
	}

}
