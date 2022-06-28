package net.edubovit.labyrinth.web.ws;

import net.edubovit.labyrinth.service.GameService;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import static net.edubovit.labyrinth.service.GameService.MovementDirection.DOWN;
import static net.edubovit.labyrinth.service.GameService.MovementDirection.LEFT;
import static net.edubovit.labyrinth.service.GameService.MovementDirection.RIGHT;
import static net.edubovit.labyrinth.service.GameService.MovementDirection.UP;

@Controller
@RequiredArgsConstructor
public class GameWebSocketController {

    private final GameService service;

    @MessageMapping("/game/move/up")
    public void moveUp() {
        service.move(UP);
    }

    @MessageMapping("/game/move/down")
    public void moveDown() {
        service.move(DOWN);
    }

    @MessageMapping("/game/move/left")
    public void moveLeft() {
        service.move(LEFT);
    }

    @MessageMapping("/game/move/right")
    public void moveRight() {
        service.move(RIGHT);
    }

}
