package cat.itacademy.s05.t01.n01.S05T01N01.controller;

import cat.itacademy.s05.t01.n01.S05T01N01.model.Game;
import cat.itacademy.s05.t01.n01.S05T01N01.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping("/new")
    public Mono<ResponseEntity<Game>> createGame(@RequestParam String playerId) {
        return gameService.createGame(playerId)
                .map(game -> ResponseEntity.status(201).body(game));
    }

    @GetMapping("/getGame/{id}")
    public Mono<ResponseEntity<Game>> getGame(@PathVariable String id) {
        return gameService.getGame(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<Void>> deleteGame(@PathVariable String id) {
        return gameService.deleteGame(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @PostMapping("/play/{id}")
    public Mono<ResponseEntity<Game>> play(@PathVariable String id, @RequestParam String playerId, @RequestParam String action) {
        return gameService.play(id, playerId, action)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
