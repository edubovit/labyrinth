package net.edubovit.labyrinth.web;

import net.edubovit.labyrinth.dto.CreateGameRequestDTO;
import net.edubovit.labyrinth.dto.GameSessionDTO;
import net.edubovit.labyrinth.service.LabyrinthApiService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LabyrinthController {

    private final LabyrinthApiService service;

    @PostMapping("create")
    public GameSessionDTO createGame(@RequestBody CreateGameRequestDTO request) {
        return service.create(request);
    }

}
