package net.edubovit.labyrinth.dto;

import java.util.Collection;

public record MovementResultDTO(Collection<CellChangeDTO> changes,
                                String owner,
                                int turns,
                                boolean finish) {

}
