package at.ac.tuwien.esse.itseclarge.lab1.DAO.Simple;

import java.util.HashMap;
import java.util.Map;

import at.ac.tuwien.esse.itseclarge.lab1.Card;
import at.ac.tuwien.esse.itseclarge.lab1.DAO.CardDAO;

public class SimpleCardDAO implements CardDAO {

	private static Map<String, Card> cards = new HashMap<String, Card>();

	@Override
	public void createCard(Card card) {
		cards.put(card.getCardno() + card.getValidity(), card);
	}

	@Override
	public void deleteCard(Card card) {
		cards.remove(card.getCardno() + card.getValidity());
	}

	@Override
	public Card readCard(String cardno, String validity) {
		if (new Card(cardno, validity).isFormallyValid()) {
			return cards.get(cardno + validity);
		} else {
			return null;
		}
	}

}
