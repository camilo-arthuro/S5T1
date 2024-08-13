package cat.itacademy.s05.t01.n01.S05T01N01.service;

import cat.itacademy.s05.t01.n01.S05T01N01.model.Card;
import cat.itacademy.s05.t01.n01.S05T01N01.model.Game;
import cat.itacademy.s05.t01.n01.S05T01N01.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

public class GameServiceTest {

    @InjectMocks
    private GameService gameService;

    @Mock
    private GameRepository gameRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateGame() {
        Game game = new Game();
        game.setPlayerId("player1");
        game.setPlayerHand(new ArrayList<>(Arrays.asList(new Card("Hearts", "2"), new Card("Clubs", "3"))));
        game.setDealerHand(new ArrayList<>(Arrays.asList(new Card("Diamonds", "4"), new Card("Spades", "5"))));
        game.setStatus("IN_PROGRESS");

        Mockito.when(gameRepository.save(Mockito.any(Game.class))).thenReturn(Mono.just(game));

        Mono<Game> createdGameMono = gameService.createGame("player1");

        StepVerifier.create(createdGameMono)
                .expectNextMatches(createdGame ->
                        createdGame.getPlayerId().equals("player1") &&
                                createdGame.getStatus().equals("IN_PROGRESS") &&
                                createdGame.getPlayerHand().size() == 2 &&
                                createdGame.getDealerHand().size() == 2)
                .verifyComplete();
    }

    @Test
    void testGetGameDetails() {
        Game mockGame = new Game();
        mockGame.setId("1");
        mockGame.setPlayerId("player1");
        when(gameRepository.findById("1")).thenReturn(Mono.just(mockGame));

        Game result = gameService.getGame("1").block();
        assertEquals(mockGame, result);
    }

    @Test
    void testPlay() {
        Game game = new Game();
        game.setId("1");
        game.setPlayerId("player1");
        game.setPlayerHand(new ArrayList<>(Arrays.asList(new Card("Hearts", "2"), new Card("Clubs", "3"))));
        game.setDealerHand(new ArrayList<>(Arrays.asList(new Card("Diamonds", "4"), new Card("Spades", "5"))));
        game.setStatus("IN_PROGRESS");

        Mockito.when(gameRepository.findById("1")).thenReturn(Mono.just(game));
        Mockito.when(gameRepository.save(Mockito.any(Game.class))).thenReturn(Mono.just(game));

        Mono<Game> playGameMono = gameService.play("1", "player1", "hit");

        StepVerifier.create(playGameMono)
                .expectNextMatches(updatedGame ->
                        updatedGame.getPlayerHand().size() == 3 &&
                                updatedGame.getStatus().equals("IN_PROGRESS"))
                .verifyComplete();
    }

    @Test
    void testDeleteGame() {
        when(gameRepository.deleteById(anyString())).thenReturn(Mono.empty());

        gameService.deleteGame("1").block();
    }

}
