package net.edubovit.labyrinth.web;

import net.edubovit.labyrinth.service.ImageService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService service;

    @GetMapping(value = "{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] findById(@PathVariable UUID id) {
        return service.findById(id);
    }

}
