package net.edubovit.labyrinth.event;

public record PlayerLeftEvent(String username) implements GameEvent {

    @Override
    public String subtopic() {
        return "leave";
    }

}
