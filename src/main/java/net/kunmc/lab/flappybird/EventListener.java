package net.kunmc.lab.flappybird;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.util.BoundingBox;

public class EventListener implements Listener {

    private Flappybird flappybird;

    public EventListener(Flappybird flappybird) {
        this.flappybird = flappybird;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().setAllowFlight(true);
        if (!shouldHandleEvent(event)) {
            return;
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        player.setAllowFlight(true);
        if (!shouldHandleEvent(event)) {
            return;
        }
        flappybird.jump(player);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!shouldHandleEvent(event)) {
            return;
        }
        Player player = event.getPlayer();
        World world = player.getWorld();
        double expand = 0.1d;
        BoundingBox bbox = new BoundingBox().copy(player.getBoundingBox()).expand(expand, expand, expand, expand, expand, expand);
        Location locationMin = bbox.getMin().toLocation(world);
        Location locationMax = bbox.getMax().toLocation(world);

        boolean isTouching = false;
        for (double i = locationMin.getX(); i <= locationMax.getX() + 1; i ++) {
            for (double j = locationMin.getY(); j <= locationMax.getY() + 1; j ++) {
                for (double k = locationMin.getZ(); k <= locationMax.getZ() + 1; k ++) {
                    Block block = player.getWorld().getBlockAt(new Location(world, i, j, k));
                    boolean flag = bbox.overlaps(block.getBoundingBox()) && !block.getType().isAir();
                    isTouching = isTouching || flag;
                }
            }
        }
        if (!isTouching) {
            return;
        }
        player.damage(1000);
    }

    @EventHandler
    public void jump(PlayerToggleFlightEvent event) {
        GameMode gamemode = event.getPlayer().getGameMode();
        if (gamemode.equals(GameMode.CREATIVE) || gamemode.equals(GameMode.SPECTATOR)) {
            return;
        }
        if (flappybird.isDebug()) {
            return;
        }
        Player player = event.getPlayer();
        if (!player.isFlying()) {
            event.setCancelled(true);
        }
        flappybird.jump(player);
    }

    public boolean shouldHandleEvent(PlayerEvent event) {
        if (!flappybird.isActive()) {
            return false;
        }
        GameMode gamemode = event.getPlayer().getGameMode();
        if (gamemode.equals(GameMode.CREATIVE) || gamemode.equals(GameMode.SPECTATOR)) {
            return false;
        }
        return true;
    }
}
