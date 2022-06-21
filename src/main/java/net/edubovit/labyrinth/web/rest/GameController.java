package net.edubovit.labyrinth.web.rest;

import net.edubovit.labyrinth.config.security.PreAuthorizeUser;
import net.edubovit.labyrinth.dto.CreateGameRequestDTO;
import net.edubovit.labyrinth.dto.GameDTO;
import net.edubovit.labyrinth.service.GameService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("game")
@RequiredArgsConstructor
public class GameController {

    private final GameService service;

    @PostMapping("create")
    @PreAuthorizeUser
    public GameDTO createGame(@RequestBody(required = false) CreateGameRequestDTO request) {
        return service.create(request == null ? CreateGameRequestDTO.defaultGame() : request);
    }

    @PostMapping("join/{hostUsername}")
    @PreAuthorizeUser
    public GameDTO joinGame(@PathVariable String hostUsername) {
        return service.join(hostUsername);
    }

    @GetMapping
    @PreAuthorizeUser
    public GameDTO getSession() {
        return service.getCurrent();
    }

}
