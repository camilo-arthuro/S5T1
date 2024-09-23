package cat.itacademy.s05.t01.n01.S05T01N01.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
public class Game {

    @Id
    private String id;
    private String playerId;
    private List<Card> playerHand;
    private List<Card> dealerHand;
    private int playerSum;
    private int dealerSum;
    private int playerAce;
    private int dealerAce;
    private String status;
}
