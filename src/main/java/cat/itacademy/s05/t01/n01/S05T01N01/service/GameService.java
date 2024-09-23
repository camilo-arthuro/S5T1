package cat.itacademy.s05.t01.n01.S05T01N01.service;

import cat.itacademy.s05.t01.n01.S05T01N01.model.Card;
import cat.itacademy.s05.t01.n01.S05T01N01.model.Deck;
import cat.itacademy.s05.t01.n01.S05T01N01.model.Game;
import cat.itacademy.s05.t01.n01.S05T01N01.model.Player;
import cat.itacademy.s05.t01.n01.S05T01N01.repository.GameRepository;
import cat.itacademy.s05.t01.n01.S05T01N01.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

    public Mono<Game> createGame(String playerId) {
        Game game = new Game();
        Player player = findPlayer(playerId);
        if (player != null){
            game.setPlayerId(playerId);
        } else {
            game.setPlayerId(playerId);
            savePlayer(playerId);
        }
        game.setPlayerHand(new ArrayList<>());
        game.setDealerHand(new ArrayList<>());
        game.setStatus("IN_PROGRESS");

        Deck deck = new Deck();

        addCardsPlayer(game, deck);
        addCardsDealer(game, deck);

        return gameRepository.save(game);
    }

    public void addCardsPlayer(Game game, Deck deck){
        for (int i = 0; i < 2; i++) {
            game.getPlayerHand().add(deck.dealCard());
            totalAcePlayer(game);
        }
        game.setPlayerSum(gameScore(game.getPlayerHand()));
    }

    public void addCardsDealer(Game game, Deck deck){
        for (int i = 0; i < 2; i++) {
            game.getDealerHand().add(deck.dealCard());
            totalAceDealer(game);
        }
        game.setDealerSum(gameScore(game.getDealerHand()));
    }

    public void savePlayer(String playerId){
        Player player = new Player();
        player.setName(playerId);
        player.setScore(0);

        playerRepository.save(player);
    }

    public int gameScore(List<Card> Hand){
        int total = 0;

        for (Card card : Hand) {
            total += card.getValue();
        }
        return total;
    }

    public Mono<Game> getGame(String gameId) {
        return gameRepository.findById(gameId);
    }

    public Mono<Void> deleteGame(String gameId) {
        return gameRepository.deleteById(gameId);
    }

    public Mono<Game> play(String gameId, String playerId, String action) {
        Player player = findPlayer(playerId);
        return gameRepository.findById(gameId)
                .flatMap(game -> {
                    if (!game.getPlayerId().equals(playerId)) {
                        return Mono.error(new IllegalStateException("INVALID_PLAYER_ID"));
                    } else if (!game.getStatus().equals("IN_PROGRESS") && !game.getStatus().equals("PLAYER_STAND")){
                        game.setStatus("GAME_OVER");
                    } else {
                        Deck deck = new Deck();
                        switch (action.toLowerCase()) {
                            case "hit":
                                game.getPlayerHand().add(deck.dealCard());
                                totalAcePlayer(game);
                                game.setPlayerSum(gameScore(game.getPlayerHand()));
                                verifyPointsPlayer(game);
                                if (game.getPlayerSum() > 21){
                                    game.setStatus("GAME_OVER");
                                }
                                break;
                            case "stand":
                                game.setStatus("PLAYER_STAND");
                                break;
                            default:
                                return Mono.error(new IllegalArgumentException("Invalid action"));
                        }
                        while (game.getStatus().equals("PLAYER_STAND")){
                            verifyPointsDealer(game);
                            gameStatus(game, player, deck);
                        }
                    }
                    return gameRepository.save(game);
                });
    }

    public void verifyPointsPlayer(Game game){
        if (game.getPlayerSum() > 21 && game.getPlayerAce() > 0){
            game.setPlayerSum(game.getPlayerSum()-(10*game.getPlayerAce()));
        }
    }

    public void verifyPointsDealer(Game game){
        if (game.getDealerSum() > 21 && game.getDealerAce() > 0){
            game.setDealerSum(game.getDealerSum()-(10*game.getDealerAce()));
        }
    }

    public void totalAcePlayer(Game game){
        String aceCard = game.getPlayerHand().getLast().getRank();
        int total = game.getPlayerAce();

        if ("A".contains(aceCard)){
            game.setPlayerAce(total+1);
        }
    }

    public void totalAceDealer(Game game){
        String aceCard = game.getDealerHand().getLast().getRank();
        int total = game.getDealerAce();

        if ("A".contains(aceCard)){
            game.setDealerAce(total+1);
        }
    }

    public void gameStatus(Game game, Player player, Deck deck){
        if (game.getDealerSum()>game.getPlayerSum() && game.getDealerSum() <=21){
            game.setStatus("PLAYER_LOSES");
        } else if (game.getDealerSum() == 21 && game.getPlayerSum() == 21) {
            game.setStatus("NO_WINNERS");
        } else if (game.getDealerSum()>21) {
            game.setStatus("PLAYER_WINS");
            player.setScore(player.getScore()+1);
            playerRepository.save(player);
        } else {
            game.getDealerHand().add(deck.dealCard());
            totalAceDealer(game);
            game.setDealerSum(gameScore(game.getDealerHand()));
        }
    }

    public Player findPlayer(String playerId){
        return playerRepository.findAll()
                .stream()
                .filter(player -> player.getName().equals(playerId))
                .findFirst()
                .orElse(null);
    }

    public List<Player> getRanking() {
        return playerRepository.findAllByOrderByScoreDesc();
    }

    public Mono<Game> changeName(String playerId, String gameId, String newPlayerId){
        Player player = findPlayer(playerId);
        if (player != null){
            player.setName(newPlayerId);
            playerRepository.save(player);
        }
        return gameRepository.findById(gameId)
                .flatMap(game -> {
                    if (!game.getPlayerId().equals(playerId)) {
                        return Mono.error(new IllegalStateException("PLAYER_NOT_FOUND"));
                    } else {
                        game.setPlayerId(newPlayerId);
                    }
                    return gameRepository.save(game);
                });
    }

}
