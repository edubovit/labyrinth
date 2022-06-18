package net.edubovit.labyrinth.service;

import net.edubovit.labyrinth.dto.MovementResultDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {

    private final SimpMessagingTemplate sender;

    public void tilesChanged(UUID gameId, MovementResultDTO event) {
        sender.convertAndSend("/topic/game/" + gameId, event);
    }

}
