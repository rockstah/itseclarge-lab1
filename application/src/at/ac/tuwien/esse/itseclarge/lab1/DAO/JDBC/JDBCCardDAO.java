package at.ac.tuwien.esse.itseclarge.lab1.DAO.JDBC;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.io.FileUtils;

import at.ac.tuwien.esse.itseclarge.lab1.Card;
import at.ac.tuwien.esse.itseclarge.lab1.DAO.CardDAO;

public class JDBCCardDAO implements CardDAO {

	private static final String JDBC_CONNECTION = "jdbc:sqlite:database/cards.db";
	private static final String CREATE_STATEMENT = "insert into cards (cardno, validity,  signature, climit, customer) values (?,?,?,?,?)";
	private static final String READ_STATEMENT = "select * from cards where cardno = ? and validity = ?";
	private static final String DELETE_STATEMENT = "delete from cards where cardno = ? and validity = ?";

	private PreparedStatement createStatement;
	private PreparedStatement deleteStatement;
	private PreparedStatement readStatement;

	/*
	 * Der Einfachheit halber sind die datenbankspezifischen Daten hier, da es nur 1 DAO gibt
	 */
	public JDBCCardDAO() {

		try {

			Class.forName("org.sqlite.JDBC");
			Connection con = DriverManager.getConnection(JDBC_CONNECTION);

			// Schema erzeugen falls nicht vorhanden
			Statement s = con.createStatement();
			s.executeUpdate(FileUtils.readFileToString(new File("database/schema.sql")));

			// Prepared Statements parsen
			createStatement = con.prepareStatement(CREATE_STATEMENT);
			readStatement = con.prepareStatement(READ_STATEMENT);
			deleteStatement = con.prepareStatement(DELETE_STATEMENT);

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			System.err.println("Something wrong with pstmt!");
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found!");
			System.err.println(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void createCard(Card card) {
		
		try {
			
			createStatement.setString(1, card.getCardno());
			createStatement.setString(2, card.getValidity());
			createStatement.setString(3, card.getSignature());

			// setBigDecimal doesn't work!
			createStatement.setString(4, card.getLimit().toPlainString());
			createStatement.setLong(5, card.getCustomer());

			createStatement.executeUpdate();
			createStatement.clearParameters();
			
		} catch (SQLException e) {
			// TODO log + errorhandling
			System.err.println(e.getMessage());
			System.err.println("createCard: Error creating " + card);
		}

	}

	@Override
	public void deleteCard(Card card) {

		try {
			deleteStatement.setString(1, card.getCardno());
			deleteStatement.executeQuery();
			deleteStatement.clearParameters();
		} catch (SQLException e) {
			// TODO log + errorhandling
			System.err.println(e.getMessage());
			System.err.println("deleteCard: not found");
		}
	}

	@Override
	public Card readCard(String cardno, String validity) {
		Card c;

		try {
			readStatement.setString(1, cardno);
			readStatement.setString(2, validity);
			ResultSet rs = readStatement.executeQuery();
			
			// wenn keine Resultate da sind, null zurückgeben
			if (rs.next() == false) {
				return null;
			}
			
			c = new Card(
				rs.getString("cardno"),
				rs.getString("validity"),
				new BigDecimal(rs.getDouble("climit")),
				rs.getLong("customer"),
				rs.getString("signature")
			);
			
			readStatement.clearParameters();
			
		} catch (SQLException e) {
			// TODO log + errorhandling
			System.out.println(e.getMessage());
			System.out.println("readCard: not found");
			return null;
		}
		
		return c;
	}

}
