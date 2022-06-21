package net.edubovit.labyrinth.web.ws;

import net.edubovit.labyrinth.service.GameService;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class GameWebSocketController {

    private final GameService service;

    @MessageMapping("/game/move/up")
    public void moveUp() {
        service.moveUp();
    }

    @MessageMapping("/game/move/down")
    public void moveDown() {
        service.moveDown();
    }

    @MessageMapping("/game/move/left")
    public void moveLeft() {
        service.moveLeft();
    }

    @MessageMapping("/game/move/right")
    public void moveRight() {
        service.moveRight();
    }

}
