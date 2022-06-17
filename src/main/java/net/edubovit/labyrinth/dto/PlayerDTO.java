package net.edubovit.labyrinth.dto;

import net.edubovit.labyrinth.entity.Player;

public record PlayerDTO(String name, int x, int y) {

    public PlayerDTO(Player player) {
        this("Xoma163", player.getPosition().getJ(), player.getPosition().getI());
    }

}
