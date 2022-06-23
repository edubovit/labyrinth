package net.edubovit.labyrinth.service;

import net.edubovit.labyrinth.event.GameEvent;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {

    private final SimpMessagingTemplate sender;

    public void sendGameEvent(UUID gameId, GameEvent event) {
        sender.convertAndSend("/topic/game/%s/%s".formatted(gameId, event.subtopic()), event);
    }

}
