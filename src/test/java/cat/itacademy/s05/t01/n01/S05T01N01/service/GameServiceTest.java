package cat.itacademy.s05.t01.n01.S05T01N01.service;

import cat.itacademy.s05.t01.n01.S05T01N01.model.Game;
import cat.itacademy.s05.t01.n01.S05T01N01.model.Player;
import cat.itacademy.s05.t01.n01.S05T01N01.repository.GameRepository;
import cat.itacademy.s05.t01.n01.S05T01N01.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private GameService gameService;

    @Test
    public void testCreateGame() {
        Game mockGame = new Game();
        mockGame.setPlayerId("player1");

        when(gameRepository.save(any(Game.class))).thenReturn(Mono.just(mockGame));
        when(playerRepository.save(any(Player.class))).thenReturn(new Player());

        Mono<Game> gameMono = gameService.createGame("player1");

        StepVerifier.create(gameMono)
                .assertNext(game -> {
                    assertEquals("player1", game.getPlayerId());
                })
                .verifyComplete();
    }
}
