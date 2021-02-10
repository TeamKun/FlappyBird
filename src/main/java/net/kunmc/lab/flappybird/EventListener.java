package net.kunmc.lab.flappybird;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

public class EventListener implements Listener {

    private Flappybird flappybird;

    public EventListener(Flappybird flappybird) {
        this.flappybird = flappybird;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        flappybird.getPlayerChargeStartTime().put(player, (long) 0);
        if (!shouldHandleEvent(event)) {
            return;
        }
        player.setTicksLived(1);
        flappybird.jump(player);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        flappybird.getPlayerChargeStartTime().remove(player);
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
        if (!shouldHandleEvent(event)) {
            return;
        }
        player.setTicksLived(1);
        flappybird.jump(player);
    }

    @EventHandler
    public void jump(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        GameMode gamemode = player.getGameMode();
        if (gamemode.equals(GameMode.CREATIVE) || gamemode.equals(GameMode.SPECTATOR)) {
            return;
        }
        if (!player.isSneaking()) {
            return;
        }
        flappybird.jump(player);
    }

    @EventHandler
    public void onSneakStart(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        GameMode gamemode = player.getGameMode();
        if (gamemode.equals(GameMode.CREATIVE) || gamemode.equals(GameMode.SPECTATOR)) {
            return;
        }
        if (player.isSneaking()) {
            return;
        }
        flappybird.getPlayerChargeStartTime().replace(player, System.currentTimeMillis());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (!event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            return;
        }
        event.setCancelled(true);
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
