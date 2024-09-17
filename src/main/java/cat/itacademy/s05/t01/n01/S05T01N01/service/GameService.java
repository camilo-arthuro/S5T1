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
        game.setPlayerId(playerId);
        game.setPlayerHand(new ArrayList<>());
        game.setDealerHand(new ArrayList<>());
        game.setStatus("IN_PROGRESS");

        Deck deck = new Deck();

        game.getPlayerHand().add(deck.dealCard());
        game.getPlayerHand().add(deck.dealCard());

        game.getDealerHand().add(deck.dealCard());
        game.getDealerHand().add(deck.dealCard());

        game.setPlayerSum(gameScore(game.getPlayerHand()));
        game.setDealerSum(gameScore(game.getDealerHand()));

        savePlayer(playerId);

        return gameRepository.save(game);
    }

    public void savePlayer(String playerId){
        Player player = new Player();
        player.setName(playerId);
        player.setScore(0);

        playerRepository.save(player);
    }

    public int gameScore(List<Card> Hand){
        int total = 0;

        for (int i = 0; i < Hand.size(); i++) {
            total += Hand.get(i).getValue();
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
        return gameRepository.findById(gameId)
                .flatMap(game -> {
                    if (!game.getPlayerId().equals(playerId)) {
                        return Mono.error(new IllegalStateException("Invalid player ID"));
                    }
                    Deck deck = new Deck();
                    switch (action.toLowerCase()) {
                        case "hit":
                            game.getPlayerHand().add(deck.dealCard());
                            break;
                        case "stand":
                            game.setStatus("PLAYER_STAND");
                            break;
                        default:
                            return Mono.error(new IllegalArgumentException("Invalid action"));
                    }
                    return gameRepository.save(game);
                });
    }

    public List<Player> getRanking() {
        return playerRepository.findAllByOrderByScoreDesc();
    }

}
