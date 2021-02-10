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
            case "start":
                if (flappybird.isActive()) {
                    sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("ゲームは進行中です！").toString());
                } else if (flappybird.isActivating()) {
                    sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("起動中です！").toString());
                } else {
                    sender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append("ゲームを開始しました").toString());
                    flappybird.start();
                }
                break;
            case "stop":
                if (!flappybird.isActive()) {
                    sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("ゲームは進行中ではありません！").toString());
                } else {
                    sender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append("ゲームを停止しました").toString());
                    flappybird.stop();
                }
                break;
            case "reload":
                flappybird.reloadConfig();
                sender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(String.format("設定ファイルを再読み込みしました")).toString());
                break;
            case "forceSpectator":
                if (args.length < 2) {
                    sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("引数が足りません！").toString());
                    return true;
                }
                if (args[1].equals("true") || args[1].equals("false")) {
                    boolean value = Boolean.parseBoolean(args[1]);
                    flappybird.setForceSpectator(value);
                    sender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(String.format("強制スペクテイターモード を %s にしました", flappybird.isForceSpectator())).toString());
                } else {
                    sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("無効な引数です！").toString());
                    return true;
                }
                break;
            case "status":
                sender.sendMessage(new StringBuilder()
                        .append(String.format("状態: %s", flappybird.isActive() ? ChatColor.GREEN + "進行中" : flappybird.isActivating() ? ChatColor.AQUA + "起動中" : ChatColor.RED + "停止中")).append("\n")
                        .append(String.format(ChatColor.RESET + "強制スペクテイターモード: %s", flappybird.isForceSpectator() ? ChatColor.GREEN + "有効" : ChatColor.RED + "無効"))
                        .toString());
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
                suggestions = new ArrayList<>(Arrays.asList("start", "stop", "reload", "forceSpectator", "status")).stream().filter(s -> s.contains(args[0])).collect(Collectors.toList());
                break;
            case 2:
                List<String> trueOrFalses = new ArrayList<>(Arrays.asList("forceSpectator"));
                if (trueOrFalses.contains(args[0])) {
                    suggestions = new ArrayList<>(Arrays.asList("true", "false")).stream().filter(s -> s.contains(args[1])).collect(Collectors.toList());
                }
                break;
            default:
                break;
        }

        return suggestions;
    }
}
