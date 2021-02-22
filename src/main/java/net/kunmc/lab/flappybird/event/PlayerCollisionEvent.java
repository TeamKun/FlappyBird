package net.kunmc.lab.flappybird.event;

import org.bukkit.entity.Player;

public class PlayerCollisionEvent extends FlappyEvent {

    public PlayerCollisionEvent(Player player) {
        super(player);
    }
}
