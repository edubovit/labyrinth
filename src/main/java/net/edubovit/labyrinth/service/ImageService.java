package net.edubovit.labyrinth.service;

import net.edubovit.labyrinth.exception.NotFoundException;
import net.edubovit.labyrinth.repository.ImageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final ImageRepository imageRepository;

    public byte[] findById(UUID id) {
        log.info("retrieving image: {}", id.toString());
        return imageRepository.get(id).orElseThrow(NotFoundException::new);
    }

}
