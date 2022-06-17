package net.edubovit.labyrinth.dto;

import net.edubovit.labyrinth.domain.Cell;

public record CellChangeDTO(int i, int j, CellDTO newState) {

    public CellChangeDTO(Cell cell) {
        this(cell.getI(), cell.getJ(), new CellDTO(cell));
    }

}
