package net.edubovit.labyrinth.dto;

import java.util.Collection;

public record GameChangedEvent(String owner,
                               int turns,
                               boolean finish,
                               Collection<CellChangeDTO> changes) {

}
