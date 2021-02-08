package net.kunmc.lab.flappybird;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Command implements TabExecutor {

    private Flappybird flappybird;

    public Command(Flappybird flappybird) {
        this.flappybird = flappybird;
    }

    public void register() {
        Bukkit.getPluginCommand("flappy").setExecutor(this);
        Bukkit.getPluginCommand("flappy").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("引数が足りません！").toString());
            return true;
        }

        switch (args[0]) {
            case "activate":
                if (flappybird.isActive()) {
                    sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("既に起動しています！").toString());
                } else {
                    flappybird.setActive(true);
                    sender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append("FlappyBird 起動").toString());
                }
                break;
            case "inactivate":
                if (!flappybird.isActive()) {
                    sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("まだ起動していません！").toString());
                } else {
                    flappybird.setActive(false);
                    sender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append("FlappyBird 停止").toString());
                }
                break;
            case "debug":
                flappybird.setDebug(!flappybird.isDebug());
                sender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(String.format("Debug を %s にしました", flappybird.isDebug())).toString());
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        List<String> suggestions = null;

        switch (args.length) {
            case 1:
                suggestions = new ArrayList<>(Arrays.asList("activate", "inactivate", "debug")).stream().filter(s -> s.contains(args[0])).collect(Collectors.toList());
                break;
            default:
                break;
        }

        return suggestions;
    }
}
