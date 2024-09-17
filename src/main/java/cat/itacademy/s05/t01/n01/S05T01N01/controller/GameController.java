package cat.itacademy.s05.t01.n01.S05T01N01.controller;

import cat.itacademy.s05.t01.n01.S05T01N01.model.Game;
import cat.itacademy.s05.t01.n01.S05T01N01.model.Player;
import cat.itacademy.s05.t01.n01.S05T01N01.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping("/new")
    public Mono<ResponseEntity<Game>> createGame(@RequestBody Map<String, String> payload) {
        String playerId = payload.get("playerId");
        return gameService.createGame(playerId)
                .map(game -> ResponseEntity.status(201).body(game));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Game>> getGame(@PathVariable String id) {
        return gameService.getGame(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/delete")
    public Mono<ResponseEntity<Void>> deleteGame(@PathVariable String id) {
        return gameService.deleteGame(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @PostMapping("/{id}/play")
    public Mono<ResponseEntity<Game>> play(@PathVariable String id, @RequestBody Map<String, String> payload) {
        String playerId = payload.get("playerId");
        String action = payload.get("action");
        return gameService.play(id, playerId, action)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<Player>> getRanking() {
        List<Player> ranking = gameService.getRanking();
        return ranking.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(ranking);
    }

}
