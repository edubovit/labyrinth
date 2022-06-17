package net.edubovit.labyrinth.dto;

import java.util.Collection;

public record MovementResultDTO(Collection<CellChangeDTO> changes,
                                Coordinates playerCoordinates,
                                int turns,
                                boolean finish) {

}
