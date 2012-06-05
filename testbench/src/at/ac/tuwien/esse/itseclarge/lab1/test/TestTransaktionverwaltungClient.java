package at.ac.tuwien.esse.itseclarge.lab1.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigDecimal;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;

public class TestTransaktionverwaltungClient {

	static final String CARD_NUMBER_VALID = "0000000000000000";
	static final String CARD_NUMBER_INVALID = "00000000000000";
	static final String CARD_VALIDITY_VALID = "05/19";
	static final String CARD_VALIDITY_INVALID_FORMAT = "invalid";
	static final String CARD_VALIDITY_INVALID_EXPIRED = "06/10";
	static final Long CARD_CUSTOMER = 42L;
	static final BigDecimal CARD_LIMIT = new BigDecimal(2500);

	private CardClient client;

	@Before
	public void setUp() {
		this.client = new TransaktionverwaltungClient();
	}

	@After
	public void tearDown() throws JSONException, IOException {
		this.client = null;
	}

	/**
	 * Stellt sicher, dass anfangs keine Karte vorhanden ist.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testEmpty() throws Exception {
		assertFalse(this.client.isValid(CARD_NUMBER_VALID, CARD_VALIDITY_VALID));
	}

	@Test
	public void testCreateUnauthorized() throws JSONException, IOException {
		Reference ref = new Reference(CardClient.ENDPOINT);

		JSONObject card = new JSONObject();
		card.put("cardno", CARD_NUMBER_VALID);
		card.put("validity", CARD_VALIDITY_VALID);
		card.put("limit", CARD_LIMIT);
		card.put("customer", CARD_CUSTOMER);
		card.put("signature", "");

		// Sollte fehlschlagen, da die Transaktionsverwaltung
		// nicht das richtige Zertifikat hat.
		try {
			this.client.makeRequest(Method.POST, ref, new JsonRepresentation(card));
			fail();
		} catch(APIException e) {
			assertEquals(e.getStatus(), Status.CLIENT_ERROR_UNAUTHORIZED);
		}
	}

	@Test
	public void testDeleteUnauthorized() throws IOException, APIException {
		Reference ref = new Reference(CardClient.ENDPOINT);
		ref.addQueryParameter("cardno", CARD_NUMBER_VALID);
		ref.addQueryParameter("validity", CARD_VALIDITY_VALID);
		
		// Sollte fehlschlagen, da die Transaktionsverwaltung
		// nicht das richtige Zertifikat hat.
		try {
			this.client.makeRequest(Method.DELETE, ref);
			fail();
		} catch(APIException e) {
			assertEquals(e.getStatus(), Status.CLIENT_ERROR_UNAUTHORIZED);
		}
	}
	
}
