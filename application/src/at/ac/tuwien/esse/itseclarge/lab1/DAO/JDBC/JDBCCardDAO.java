package at.ac.tuwien.esse.itseclarge.lab1.DAO.JDBC;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
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

	private String conString = "jdbc:sqlite:database/cards.db";

	PreparedStatement create_pstmt = null;
	PreparedStatement delete_pstmt = null;
	PreparedStatement read_pstmt = null;

	/*
	 * Der Einfachheit halber sind die datenbankspezifischen Daten hier, da es nur 1 DAO gibt
	 */
	public JDBCCardDAO() {

		try {

			Class.forName("org.sqlite.JDBC");

			Connection con = DriverManager.getConnection(conString);
			
			// Schema erzeugen falls nicht vorhanden
			Statement s = con.createStatement();
			s.executeUpdate(FileUtils.readFileToString(new File("database/schema.sql")));

			create_pstmt = con.prepareStatement("insert into cards (cardno, validity,  signature, climit, customer) values (?,?,?,?,?)");
			// create_pstmt = con.prepareStatement( "insert into cards values (?,?,?,?,?)");

			read_pstmt = con.prepareStatement("SELECT cardno, validity,  signature, climit, customer FROM cards");
			delete_pstmt = con.prepareStatement("delete from cards where cardno = ?");

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
			create_pstmt.setString(1, card.getCardno());
			create_pstmt.setString(2, card.getValidity());
			create_pstmt.setString(3, card.getSignature());
			create_pstmt.setString(4, card.getLimit().toPlainString()); // setBigDecimal doesent work!
			create_pstmt.setLong(5, card.getCustomer());
			// create_pstmt.setString(4, card.getLimit().toString());
			// create_pstmt.setLong(5, card.getCustomer());

			// DEBUG
			System.err.println(create_pstmt.toString());

			create_pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO log + errorhandling
			System.err.println(e.getMessage());
			System.err.println("createCard: Error creating " + card.getCardno() + ":"
					+ card.getValidity() + " " + card.getSignature() + " " + card.getLimit() + " "
					+ card.getCustomer());
		}

	}

	@Override
	public void deleteCard(Card card) {

		try {
			delete_pstmt.setString(1, card.getCardno());
			delete_pstmt.executeQuery();
		} catch (SQLException e) {
			// TODO log + errorhandling
			System.err.println(e.getMessage());
			System.err.println("deleteCard: not found");
		}
	}

	@Override
	public Card readCard(String cardno, String validity) {
		Card c = new Card();

		try {
			ResultSet rs = read_pstmt.executeQuery();

			while (rs.next()) {
				// TODO check auf 1 Datensatz (bzw unique in DB setzen)
				c.setCardno(rs.getString("cardno"));
				c.setCustomer(rs.getLong("customer"));
				c.setLimit(new BigDecimal(rs.getString("climit")));
				c.setSignature(rs.getString("signature"));
				c.setValidity(rs.getString("validity"));
			}
		} catch (SQLException e) {
			// TODO log + errorhandling
			System.err.println(e.getMessage());
			System.err.println("readCard: not found");
			return null;
		}

		return c;
	}

}
