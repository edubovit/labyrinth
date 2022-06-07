package net.edubovit.labyrinth.web;

import net.edubovit.labyrinth.dto.CreateGameRequestDTO;
import net.edubovit.labyrinth.dto.GameSessionDTO;
import net.edubovit.labyrinth.service.LabyrinthApiService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("game")
@RequiredArgsConstructor
public class LabyrinthController {

    private final LabyrinthApiService service;

    @PostMapping("create")
    public GameSessionDTO createGame(@RequestBody(required = false) CreateGameRequestDTO request) {
        return service.create(request == null ? CreateGameRequestDTO.defaultGame() : request);
    }

    @PostMapping("{id}/up")
    public GameSessionDTO moveUp(@PathVariable UUID id) {
        return service.moveUp(id);
    }

    @PostMapping("{id}/down")
    public GameSessionDTO moveDown(@PathVariable UUID id) {
        return service.moveDown(id);
    }

    @PostMapping("{id}/left")
    public GameSessionDTO moveLeft(@PathVariable UUID id) {
        return service.moveLeft(id);
    }

    @PostMapping("{id}/right")
    public GameSessionDTO moveRight(@PathVariable UUID id) {
        return service.moveRight(id);
    }

}
