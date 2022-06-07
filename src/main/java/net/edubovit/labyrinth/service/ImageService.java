package net.edubovit.labyrinth.service;

import net.edubovit.labyrinth.exception.NotFoundException;
import net.edubovit.labyrinth.repository.ImageRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.awt.Image;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    public byte[] findById(UUID id) {
        return imageRepository.get(id).orElseThrow(NotFoundException::new);
    }

}
