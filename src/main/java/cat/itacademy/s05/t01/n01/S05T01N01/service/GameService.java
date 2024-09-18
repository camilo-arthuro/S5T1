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
                        return Mono.error(new IllegalStateException("Invalid player ID"));
                    }
                    Deck deck = new Deck();
                    switch (action.toLowerCase()) {
                        case "hit":
                            game.getPlayerHand().add(deck.dealCard());
                            game.setPlayerSum(gameScore(game.getPlayerHand()));
                            break;
                        case "stand":
                            game.setStatus("PLAYER_STAND");
                            break;
                        default:
                            return Mono.error(new IllegalArgumentException("Invalid action"));
                    }
                    while (game.getStatus().equals("PLAYER_STAND")){
                        gameStatus(game, player, deck);
                    }
                    return gameRepository.save(game);
                });
    }

    public void gameStatus(Game game, Player player, Deck deck){
        if (game.getDealerSum()>game.getPlayerSum() && game.getDealerSum() <=21){
            game.setStatus("PLAYER_LOSES");
        } else if (game.getDealerSum()>21) {
            game.setStatus("PLAYER_WINS");
            player.setScore(player.getScore()+1);
            playerRepository.save(player);
        } else {
            game.getDealerHand().add(deck.dealCard());
            game.setDealerSum(gameScore(game.getDealerHand()));
        }
    }

    public Player findPlayer(String playerId){
        return playerRepository.findAll()
                .stream()
                .filter(player -> player.getName().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Player not found"));
    }

    public List<Player> getRanking() {
        return playerRepository.findAllByOrderByScoreDesc();
    }

    public Mono<Game> changeName(String playerId, String gameId, String newPlayerId){
        Player player = findPlayer(playerId);
        player.setName(newPlayerId);
        playerRepository.save(player);
        return gameRepository.findById(gameId)
                .flatMap(game -> {
                    game.setPlayerId(newPlayerId);
                    return gameRepository.save(game);
                });
    }

}
