package net.kunmc.lab.flappybird.event;

import org.bukkit.entity.Player;

public class PlayerJumpEvent extends FlappyEvent {

    private double jumpMin, jumpMax, ratio;

    public PlayerJumpEvent(Player player, double jumpMin, double jumpMax, double ratio) {
        super(player);
        this.jumpMax = jumpMax;
        this.jumpMin = jumpMin;
        this.ratio = ratio;
    }

    public double getJumpMin() {
        return jumpMin;
    }

    public void setJumpMin(double jumpMin) {
        this.jumpMin = jumpMin;
    }

    public double getJumpMax() {
        return jumpMax;
    }

    public void setJumpMax(double jumpMax) {
        this.jumpMax = jumpMax;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }
}
