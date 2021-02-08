package net.kunmc.lab.flappybird;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class Flappybird extends JavaPlugin {

    private boolean active = false;
    private boolean activating = false;
    private boolean debug = false;
    private boolean clickMode = false;
    private boolean forceSpectator = false;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new EventListener(this), this);
        new Command(this).register();
        new Task(this).runTaskTimer(this, 0, 1);
        saveDefaultConfig();

        Bukkit.getOnlinePlayers().forEach(player -> player.setAllowFlight(true));
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> player.setAllowFlight(false));
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
                if (count == 5 && isClickMode()) {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.getInventory().clear();
                        player.getInventory().addItem(new ItemStack(Material.FEATHER));
                    });
                }
                if (count > 0) {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.sendTitle(String.valueOf(count), isClickMode() ? "羽根を持ってクリックでジャンプ！" : "スペースを２回押してジャンプ！", 0, 25, 10);
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, SoundCategory.NEUTRAL, 1, 1);
                    });
                    count --;
                    return;
                } else if (count == 0) {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.sendTitle("スタート！", isClickMode() ? "羽根を持ってクリックでジャンプ！" : "スペースを２回押してジャンプ！", 0, 25, 10);
                        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.NEUTRAL, 1, 1);
                        jump(player);
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
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void jump(Player player) {
        double power = getConfig().getDouble("jumpPower", 1.0);
        player.setVelocity(player.getVelocity().setY(power));
    }

    public boolean isActivating() {
        return activating;
    }

    public boolean isClickMode() {
        return clickMode;
    }

    public void setClickMode(boolean clickMode) {
        this.clickMode = clickMode;
    }

    public boolean isForceSpectator() {
        return forceSpectator;
    }

    public void setForceSpectator(boolean forceSpectator) {
        this.forceSpectator = forceSpectator;
    }
}
