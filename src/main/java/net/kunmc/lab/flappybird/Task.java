package net.kunmc.lab.flappybird;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class Task extends BukkitRunnable {

    private Flappybird flappybird;
    private int count = 1;

    public Task(Flappybird flappybird) {
        this.flappybird = flappybird;
    }

    @Override
    public void run() {
        if (!flappybird.isActive()) {
            return;
        }
        Bukkit.getOnlinePlayers().forEach(player -> {
            GameMode gamemode = player.getGameMode();
            if (gamemode.equals(GameMode.CREATIVE) || gamemode.equals(GameMode.SPECTATOR)) {
                return;
            }
            if (!flappybird.isTraining()) {
                collisionCheck(player);
            }
            if (flappybird.getConfig().getInt("tutorialTick", 0) > count || flappybird.isTraining()) {
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
        if (player.getTicksLived() < 40) {
            return;
        }
        player.damage(1000);
    }

    private void move(Player player) {
        if (player.isDead()) {
            return;
        }
        Vector vector = player.getVelocity();
        double x = flappybird.getConfig().getDouble("x", 0);
        double z = flappybird.getConfig().getDouble("z", 0);
        Vector v1 = new Vector(x, 0, z);
        double forward = flappybird.getConfig().getDouble("forward", 0);
        double right = flappybird.getConfig().getDouble("right", 0);
        Vector v2 = new Vector(forward, 0, right).rotateAroundY(Math.toRadians((- 90 - player.getLocation().getYaw())));
        if (v1.length() == 0 && v2.length() == 0) {
            return;
        }
        if (player.getTicksLived() < 40) {
            return;
        }
        vector.add(v1).add(v2);
        player.setVelocity(vector);
    }
}
