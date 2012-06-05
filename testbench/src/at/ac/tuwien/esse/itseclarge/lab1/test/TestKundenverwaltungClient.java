package at.ac.tuwien.esse.itseclarge.lab1.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.Status;

import at.ac.tuwien.esse.itseclarge.lab1.DAO.JDBC.JDBCCardDAO;

public class TestKundenverwaltungClient {

	static final String CARD_NUMBER_VALID = "0000000000000000";
	static final String CARD_NUMBER_INVALID = "00000000000000";
	static final String CARD_VALIDITY_VALID = "05/19";
	static final String CARD_VALIDITY_INVALID_FORMAT = "invalid";
	static final String CARD_VALIDITY_INVALID_EXPIRED = "06/10";
	static final Long CARD_CUSTOMER = 42L;
	static final BigDecimal CARD_LIMIT = new BigDecimal(2500);

	private KundenverwaltungClient client;

	@Before
	public void setUp() {
		this.client = new KundenverwaltungClient();
	}

	@After
	public void tearDown() throws JSONException, IOException {
		try {
			this.client.delete(CARD_NUMBER_VALID, CARD_VALIDITY_VALID);
		} catch (APIException e) {
			// ignore, since this is optimistic cleanup
		}
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

	/**
	 * Erstellt eine neue Karte. Danach sollte die Karte gültig sein.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreate() throws Exception {
		assertTrue(this.client.create(CARD_NUMBER_VALID, CARD_VALIDITY_VALID, CARD_LIMIT, CARD_CUSTOMER));
		assertTrue(this.client.isValid(CARD_NUMBER_VALID, CARD_VALIDITY_VALID));
		assertTrue(this.client.limit(CARD_NUMBER_VALID, CARD_VALIDITY_VALID).equals(CARD_LIMIT));
	}

	/**
	 * Es soll nicht möglich sein, eine abgelaufene Karte zu erstellen.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCreateExpired() throws IOException {
		try {
			this.client.create(CARD_NUMBER_VALID, CARD_VALIDITY_INVALID_EXPIRED, CARD_LIMIT,
					CARD_CUSTOMER);
		} catch (APIException e) {
			assertEquals(e.getStatus(), Status.CLIENT_ERROR_BAD_REQUEST);
		}
	}

	/**
	 * Es soll nicht möglich sein, eine Karte mit ungültiger Nummer zu erstellen.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCreateInvalidNumber() throws IOException {
		try {
			this.client.create(CARD_NUMBER_INVALID, CARD_VALIDITY_VALID, CARD_LIMIT, CARD_CUSTOMER);
			fail();
		} catch (APIException e) {
			assertEquals(e.getStatus(), Status.CLIENT_ERROR_BAD_REQUEST);
		}
	}

	/**
	 * Dieser Test simuliert einen Hackerangriff auf die Datenbank.
	 * Eine Karte wird direkt in die Datenbank geschrieben, allerdings ohne gültige Signatur.
	 * Der Server erkennt die Karte als ungültig und lässt keine Validierung zu.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDatabaseHack() throws Exception {
		Class.forName("org.sqlite.JDBC");
		Connection con = DriverManager.getConnection(JDBCCardDAO.JDBC_CONNECTION);
		PreparedStatement p = con.prepareStatement(JDBCCardDAO.CREATE_STATEMENT);
		p.setString(1, CARD_NUMBER_VALID);
		p.setString(2, CARD_VALIDITY_VALID);

		// Wir können keine Signatur erzeugen.
		p.setString(3, "");

		p.setString(4, CARD_LIMIT.toPlainString());
		p.setLong(5, CARD_CUSTOMER);

		p.executeUpdate();
		p.clearParameters();

		con.close();

		// Versuche die Karte über den Server zu validieren...
		assertFalse(this.client.isValid(CARD_NUMBER_VALID, CARD_VALIDITY_VALID));
	}

}
