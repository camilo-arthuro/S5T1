package cat.itacademy.s05.t01.n01.S05T01N01.controller;

import cat.itacademy.s05.t01.n01.S05T01N01.model.Game;
import cat.itacademy.s05.t01.n01.S05T01N01.service.GameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameControllerTest {

    @Mock
    private GameService gameService;

    @InjectMocks
    private GameController gameController;

    @Test
    public void testCreateGame() {
        Game mockGame = new Game();
        mockGame.setPlayerId("player1");

        when(gameService.createGame(anyString())).thenReturn(Mono.just(mockGame));

        Map<String, String> payload = new HashMap<>();
        payload.put("playerId", "player1");

        Mono<ResponseEntity<Game>> responseEntityMono = gameController.createGame(payload);

        StepVerifier.create(responseEntityMono)
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
                    assertEquals(mockGame, responseEntity.getBody());
                })
                .verifyComplete();
    }
}
