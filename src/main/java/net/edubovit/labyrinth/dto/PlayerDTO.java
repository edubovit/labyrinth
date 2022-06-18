package net.edubovit.labyrinth.dto;

import net.edubovit.labyrinth.domain.Player;

public record PlayerDTO(String username, int x, int y) {

    public PlayerDTO(Player player) {
        this(player.getUsername(), player.getPosition().getJ(), player.getPosition().getI());
    }

}
