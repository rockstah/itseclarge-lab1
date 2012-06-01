package at.ac.tuwien.esse.itseclarge.lab1;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.ws.rs.Consumes;
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
import at.ac.tuwien.esse.itseclarge.lab1.DAO.JDBC.JDBCCardDAO;
import at.ac.tuwien.esse.itseclarge.lab1.DAO.Simple.SimpleCardDAO;

/**
 * Die Resource auf der alle Operationen spezifiziert sind.
 * 
 * @author stephan
 */
@Path("card")
public class CardResource {

	//  Für JDBCCardDAO Datenbankparameter im DAO setzen!
		private CardDAO cardDAO = new JDBCCardDAO();
	//private CardDAO cardDAO = new SimpleCardDAO();
	
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

	@GET
	@Path("isValid")
	@Produces("application/json")
	public Response isValid(@QueryParam("cardno") String cardno,
			@QueryParam("validity") String validity) {
		
		Card c = cardDAO.readCard(cardno, validity);

		if (c == null) {
			return CardResponse.clientError("This card does not exist. Or parameters are invalid.");
		}

		return CardResponse.single(c.isValid());
	}
	
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
		
		cardDAO.createCard(c);
		return CardResponse.single(true);
	}

}
