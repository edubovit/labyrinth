package net.edubovit.labyrinth.dto;

import java.util.Collection;

public record MovementResultDTO(String owner,
                                int turns,
                                boolean finish,
                                Collection<CellChangeDTO> changes) {

}
