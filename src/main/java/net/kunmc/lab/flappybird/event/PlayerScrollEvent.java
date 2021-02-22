package net.kunmc.lab.flappybird.event;

import org.bukkit.entity.Player;

public class PlayerScrollEvent extends FlappyEvent {
    private double x, z, forward, right;

    public PlayerScrollEvent(Player player, double x, double z, double forward, double right) {
        super(player);
        this.x = x;
        this.z = z;
        this.forward = forward;
        this.right = right;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getForward() {
        return forward;
    }

    public void setForward(double forward) {
        this.forward = forward;
    }

    public double getRight() {
        return right;
    }

    public void setRight(double right) {
        this.right = right;
    }
}
