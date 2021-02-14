package net.kunmc.lab.flappybird;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public final class Flappybird extends JavaPlugin {

    private boolean active = false;
    private boolean activating = false;
    private boolean forceSpectator = false;
    public final String TITLE = "スニークキーを押してジャンプ！";
    public final String ACTIONBAR = "スニークキーを押してジャンプ！ スニークの長さでジャンプ力が変わるよ！";

    private Map<Player, Long> playerChargeStartTime = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new EventListener(this), this);
        new Command(this).register();
        new Task(this).runTaskTimer(this, 0, 1);
        saveDefaultConfig();

        Bukkit.getOnlinePlayers().forEach(player -> {
            playerChargeStartTime.put(player, (long) 0);
        });
    }

    public boolean isActive() {
        return active;
    }

    public void start() {
        activating = true;
        new BukkitRunnable() {
            int count = 5;
            @Override
            public void run() {
                if (count > 0) {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.sendTitle(String.valueOf(count), TITLE, 0, 25, 10);
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, SoundCategory.NEUTRAL, 1, 1);
                    });
                    count --;
                    return;
                } else if (count == 0) {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.sendTitle("スタート！", TITLE, 0, 25, 10);
                        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.NEUTRAL, 1, 1);
                        forceJump(player);
                    });
                } else if (count < 0) {
                    active = true;
                    activating = false;
                    cancel();
                }
                count --;
            }
        }.runTaskTimer(this, 20, 20);
    }

    public void stop() {
        active = false;
        String message = "ゲーム終了！";
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendTitle(message, "", 0, 25, 10);
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.NEUTRAL, 1, 1);
        });
    }

    public void jump(Player player) {
        double power = getConfig().getDouble("jumpMin", 0);
        long chargeTime = System.currentTimeMillis() - getPlayerChargeStartTime().get(player);
        double ratio = (chargeTime / 50) * getConfig().getDouble("ratio", 0) + 1.0;
        power = Math.min(power * ratio, getConfig().getDouble("jumpMax", 0));
        Vector vector = player.getVelocity().setY(power);
        player.setVelocity(vector);

        float pitch = (float) Math.max(Math.min(getConfig().getDouble("pitch", 0.5), 2.0), 0.5);
        player.getWorld().playSound(player.getLocation(), "jump", SoundCategory.NEUTRAL, 1.0f, pitch);
        Vector add = new Vector(1, 0, 0);
        double radis = 1;
        double delta = 15;
        add.multiply(radis);
        for (double rotation = 0 ; rotation < 360 ; rotation += delta) {
            player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation().toVector().add(add).toLocation(player.getWorld()), 1, 0, 0 ,0, 0);
            add.rotateAroundY(Math.toRadians(rotation));
        }
    }

    public void forceJump(Player player) {
        double power = getConfig().getDouble("forceJump", 1.0);
        Vector vector = player.getVelocity().setY(power);
        player.setVelocity(vector);
    }

    public boolean isActivating() {
        return activating;
    }

    public boolean isForceSpectator() {
        return forceSpectator;
    }

    public void setForceSpectator(boolean forceSpectator) {
        this.forceSpectator = forceSpectator;
    }

    public Map<Player, Long> getPlayerChargeStartTime() {
        return playerChargeStartTime;
    }
}
