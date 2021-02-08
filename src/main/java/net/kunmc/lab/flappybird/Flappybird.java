package net.kunmc.lab.flappybird;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Flappybird extends JavaPlugin {

    private boolean active = false;
    private boolean debug = false;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new EventListener(this), this);
        new Command(this).register();
        //saveDefaultConfig();

        Bukkit.getOnlinePlayers().forEach(player -> player.setAllowFlight(true));
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> player.setAllowFlight(false));
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void jump(Player player) {
        player.setVelocity(player.getVelocity().setY(1));
    }
}
