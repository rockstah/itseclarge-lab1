package at.ac.tuwien.esse.itseclarge.lab1.test;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import junit.framework.TestCase;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import at.ac.tuwien.esse.itseclarge.lab1.DAO.JDBC.JDBCCardDAO;

public class TestKundenverwaltungClient extends TestCase {

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
		this.client.delete(CARD_NUMBER_VALID, CARD_VALIDITY_VALID);
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
	}

	/**
	 * Es soll nicht möglich sein, eine abgelaufene Karte zu erstellen.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateExpired() throws Exception {
		assertFalse(this.client.create(CARD_NUMBER_VALID, CARD_VALIDITY_INVALID_EXPIRED, CARD_LIMIT,
				CARD_CUSTOMER));
	}
	
	/**
	 * Es soll nicht möglich sein, eine Karte mit ungültiger Nummer zu erstellen.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateInvalidNumber() throws Exception {
		assertFalse(this.client.create(CARD_NUMBER_INVALID, CARD_VALIDITY_VALID, CARD_LIMIT, CARD_CUSTOMER));
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

		// setBigDecimal doesn't work!
		p.setString(4, CARD_LIMIT.toPlainString());
		p.setLong(5, CARD_CUSTOMER);

		p.executeUpdate();
		p.clearParameters();
		
		// Versuche die Karte über den Server zu validieren...
		assertFalse(this.client.isValid(CARD_NUMBER_VALID, CARD_VALIDITY_VALID));
	}

}
