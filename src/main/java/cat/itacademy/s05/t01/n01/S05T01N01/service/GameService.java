package cat.itacademy.s05.t01.n01.S05T01N01.service;

import cat.itacademy.s05.t01.n01.S05T01N01.model.Deck;
import cat.itacademy.s05.t01.n01.S05T01N01.model.Game;
import cat.itacademy.s05.t01.n01.S05T01N01.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

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

        return gameRepository.save(game);
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

}
