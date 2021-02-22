package net.kunmc.lab.flappybird;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class EventListener implements Listener {

    private Flappybird flappybird;

    public EventListener(Flappybird flappybird) {
        this.flappybird = flappybird;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        flappybird.getPlayerChargeStartTime().putIfAbsent(player, (long) 0);
        flappybird.getPlayerStartTime().putIfAbsent(player, (long) 0);
        flappybird.getPlayerJumpCount().putIfAbsent(player, 0);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        flappybird.getPlayerChargeStartTime().remove(player);
        flappybird.getPlayerStartTime().remove(player);
        flappybird.getPlayerJumpCount().remove(player);
        flappybird.leave(player);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (!flappybird.isActive()) {
            return;
        }
        if (!flappybird.isjoining(event.getEntity())) {
            return;
        }
        event.setDeathMessage(String.format("%s は壁に衝突してしまった", event.getEntity().getName()));
        if (flappybird.getConfig().getBoolean("forceSpectator")) {
            event.getEntity().setGameMode(GameMode.SPECTATOR);
        }
    }

    @EventHandler
    public void jump(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!player.isSneaking()) {
            return;
        }
        if (!flappybird.isJumpable(player)) {
            return;
        }
        flappybird.jump(player);
    }

    @EventHandler
    public void onSneakStart(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (player.isSneaking()) {
            return;
        }
        flappybird.getPlayerChargeStartTime().replace(player, System.currentTimeMillis());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (!flappybird.isJumpable(player)) {
            return;
        }
        event.setCancelled(true);
    }
}
