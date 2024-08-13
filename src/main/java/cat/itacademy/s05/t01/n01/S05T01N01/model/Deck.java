package cat.itacademy.s05.t01.n01.S05T01N01.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    private List<Card> cards;

    public Deck() {
        this.cards = createDeck();
    }

    public ArrayList<Card> createDeck(){
        ArrayList<Card> newDeck = new ArrayList<>();
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};

        for (String suit : suits) {
            for (String rank : ranks) {
                newDeck.add(new Card(suit, rank));
            }
        }
        Collections.shuffle(newDeck);
        return newDeck;
    }

    public Card dealCard() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("No more cards in the deck");
        }
        return cards.remove(0);
    }

}
