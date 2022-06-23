package net.edubovit.labyrinth.event;

import net.edubovit.labyrinth.dto.CellChangeDTO;

import java.util.Collection;

public record TilesChangedEvent(String owner,
                                int turns,
                                boolean finish,
                                Collection<CellChangeDTO> changes) implements GameEvent {

    @Override
    public String subtopic() {
        return "tiles";
    }

}
