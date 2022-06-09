package net.edubovit.labyrinth.web;

import net.edubovit.labyrinth.dto.LabyrinthDTO;
import net.edubovit.labyrinth.service.LabyrinthService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("labyrinth")
@CrossOrigin({ "http://localhost:63342", "http://192.168.1.10:8001" })
@RequiredArgsConstructor
public class LabyrinthController {

    private final LabyrinthService service;

    @GetMapping("{id}")
    public LabyrinthDTO getLabyrinth(@PathVariable UUID id) {
        return service.getLabyrinth(id);
    }

}
