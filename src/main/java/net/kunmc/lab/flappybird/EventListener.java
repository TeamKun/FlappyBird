package net.kunmc.lab.flappybird;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener {

    private Flappybird flappybird;

    public EventListener(Flappybird flappybird) {
        this.flappybird = flappybird;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setAllowFlight(true);
        if (!shouldHandleEvent(event)) {
            return;
        }
        player.setTicksLived(1);
        flappybird.jump(player);
        if (flappybird.isClickMode()) {
            player.getInventory().addItem(new ItemStack(Material.FEATHER));
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (!flappybird.isActive()) {
            return;
        }
        event.setDeathMessage(String.format("%s は壁に衝突してしまった", event.getEntity().getName()));
        event.getDrops().clear();
        if (flappybird.isForceSpectator()) {
            event.getEntity().setGameMode(GameMode.SPECTATOR);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        player.setAllowFlight(true);
        if (!shouldHandleEvent(event)) {
            return;
        }
        player.setTicksLived(1);
        flappybird.jump(player);
        if (flappybird.isClickMode()) {
            player.getInventory().addItem(new ItemStack(Material.FEATHER));
        }
    }

    @EventHandler
    public void jump(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        GameMode gamemode = player.getGameMode();
        if (gamemode.equals(GameMode.CREATIVE) || gamemode.equals(GameMode.SPECTATOR)) {
            return;
        }
        if (flappybird.isDebug()) {
            return;
        }
        if (!player.isFlying()) {
            event.setCancelled(true);
        }
        flappybird.jump(player);
    }

    @EventHandler
    public void jump(PlayerInteractEvent event) {
        if (!flappybird.isClickMode()) {
            return;
        }
        GameMode gamemode = event.getPlayer().getGameMode();
        if (gamemode.equals(GameMode.CREATIVE) || gamemode.equals(GameMode.SPECTATOR)) {
            return;
        }
        ItemStack itemStack = event.getItem();
        if (itemStack == null) {
            return;
        }
        if (!itemStack.getType().equals(Material.FEATHER)) {
            return;
        }
        Player player = event.getPlayer();
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
