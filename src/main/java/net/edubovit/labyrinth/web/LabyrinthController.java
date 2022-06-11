package net.edubovit.labyrinth.web;

import net.edubovit.labyrinth.config.URIPaths;
import net.edubovit.labyrinth.dto.LabyrinthDTO;
import net.edubovit.labyrinth.service.LabyrinthService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(URIPaths.ROOT_LABYRINTH)
@RequiredArgsConstructor
public class LabyrinthController {

    private final LabyrinthService service;

    @GetMapping("{id}")
    public LabyrinthDTO getLabyrinth(@PathVariable UUID id) {
        return service.getLabyrinth(id);
    }

}
