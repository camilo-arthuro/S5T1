package cat.itacademy.s05.t01.n01.S05T01N01.controller;

import cat.itacademy.s05.t01.n01.S05T01N01.model.Card;
import cat.itacademy.s05.t01.n01.S05T01N01.model.Game;
import cat.itacademy.s05.t01.n01.S05T01N01.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class GameControllerTest {

    @InjectMocks
    private GameController gameController;

    @Mock
    private GameService gameService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        webTestClient = WebTestClient.bindToController(gameController).build();
    }

    @Test
    void testCreateGame() {
        Game mockGame = new Game();
        mockGame.setId("1");
        mockGame.setPlayerId("player1");
        mockGame.setStatus("IN_PROGRESS");
        mockGame.setPlayerHand(new ArrayList<>(Arrays.asList(new Card("Hearts", "2"), new Card("Clubs", "3"))));
        mockGame.setDealerHand(new ArrayList<>(Arrays.asList(new Card("Diamonds", "4"), new Card("Spades", "5"))));

        Mockito.when(gameService.createGame("player1")).thenReturn(Mono.just(mockGame));

        webTestClient.post()
                .uri("/game/new?playerId=player1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Game.class)
                .isEqualTo(mockGame);
    }

    @Test
    void testGetGameDetails() {
        Game mockGame = new Game();
        mockGame.setId("1");
        mockGame.setPlayerId("player1");
        when(gameService.getGame("1")).thenReturn(Mono.just(mockGame));

        webTestClient.get()
                .uri("/game/getGame/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Game.class)
                .isEqualTo(mockGame);
    }

    @Test
    void testPlay() {
        Game mockGame = new Game();
        mockGame.setId("1");
        mockGame.setPlayerId("player1");
        when(gameService.play("1", "player1", "hit")).thenReturn(Mono.just(mockGame));

        webTestClient.post()
                .uri("/game/play/1?playerId=player1&action=hit")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Game.class)
                .isEqualTo(mockGame);
    }

    @Test
    void testDeleteGame() {
        when(gameService.deleteGame("1")).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/game/delete/1")
                .exchange()
                .expectStatus().isNoContent();
    }

}
