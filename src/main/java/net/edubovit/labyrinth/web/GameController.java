package net.edubovit.labyrinth.web;

import net.edubovit.labyrinth.config.URIPaths;
import net.edubovit.labyrinth.config.security.PreAuthorizeUser;
import net.edubovit.labyrinth.dto.CreateGameRequestDTO;
import net.edubovit.labyrinth.dto.GameDTO;
import net.edubovit.labyrinth.dto.MovementResultDTO;
import net.edubovit.labyrinth.service.GameService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URIPaths.ROOT_GAME)
@RequiredArgsConstructor
public class GameController {

    private final GameService service;

    @PostMapping("create")
    @PreAuthorizeUser
    public GameDTO createGame(@RequestBody(required = false) CreateGameRequestDTO request) {
        return service.create(request == null ? CreateGameRequestDTO.defaultGame() : request);
    }

    @GetMapping
    @PreAuthorizeUser
    public GameDTO getSession() {
        return service.getCurrent();
    }

    @PostMapping("up")
    @PreAuthorizeUser
    public MovementResultDTO moveUp() {
        return service.moveUp();
    }

    @PostMapping("down")
    @PreAuthorizeUser
    public MovementResultDTO moveDown() {
        return service.moveDown();
    }

    @PostMapping("left")
    @PreAuthorizeUser
    public MovementResultDTO moveLeft() {
        return service.moveLeft();
    }

    @PostMapping("right")
    @PreAuthorizeUser
    public MovementResultDTO moveRight() {
        return service.moveRight();
    }

}
