package net.kunmc.lab.flappybird;

import net.kunmc.lab.flappybird.event.PlayerCollisionEvent;
import net.kunmc.lab.flappybird.event.PlayerScrollEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class Task extends BukkitRunnable {

    private Flappybird flappybird;
    private int count = 0;

    public Task(Flappybird flappybird) {
        this.flappybird = flappybird;
    }

    @Override
    public void run() {
        if (!flappybird.isActive()) {
            return;
        }
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (!flappybird.isjoining(player)) {
                return;
            }
            if (!flappybird.getConfig().getBoolean("training") && count % Math.max(flappybird.getConfig().getInt("collisionTick", 1), 1) == 0) {
                collisionCheck(player);
            }
            if (flappybird.getConfig().getInt("tutorialTick", 0) > flappybird.getPlayerRespawnTime().get(player) || flappybird.getConfig().getBoolean("training")) {
                player.sendActionBar(flappybird.ACTIONBAR);
            }
            move(player);
        });
        count ++;
    }

    private void collisionCheck(Player player) {
        World world = player.getWorld();
        double expand = flappybird.getConfig().getDouble("distance", 0);
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
        if (player.isDead()) {
            return;
        }
        double gametime = (System.currentTimeMillis() - flappybird.getPlayerRespawnTime().get(player)) / 20;
        if (gametime < flappybird.getConfig().getInt("noCollisionTick", 40)) {
            return;
        }

        PlayerCollisionEvent event = new PlayerCollisionEvent(player);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }
        flappybird.respawn(player);
    }

    private void move(Player player) {
        if (player.isDead()) {
            return;
        }
        Vector vector = player.getVelocity();
        double x = flappybird.getConfig().getDouble("x", 0);
        double z = flappybird.getConfig().getDouble("z", 0);
        double forward = flappybird.getConfig().getDouble("forward", 0);
        double right = flappybird.getConfig().getDouble("right", 0);

        PlayerScrollEvent event = new PlayerScrollEvent(player, x, z, forward, right);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        Vector v1 = new Vector(event.getX(), 0, event.getZ());
        Vector v2 = new Vector(event.getForward(), 0, event.getRight()).rotateAroundY(Math.toRadians((- 90 - player.getLocation().getYaw())));
        if (v1.length() == 0 && v2.length() == 0) {
            return;
        }
        vector.add(v1).add(v2);
        player.setVelocity(vector);
    }
}
