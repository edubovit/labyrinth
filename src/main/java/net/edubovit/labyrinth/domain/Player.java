package net.edubovit.labyrinth.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Player implements Serializable {

    @EqualsAndHashCode.Include
    private final String username;

    private Cell position;

    private int turns;

}
