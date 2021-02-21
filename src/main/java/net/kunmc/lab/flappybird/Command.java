package net.kunmc.lab.flappybird;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.io.File;
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
            case "join":
                join(sender, command, label, args);
                break;
            case "leave":
                leave(sender, command, label, args);
                break;
            case "activate":
                if (flappybird.isActive()) {
                    sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("フラッピーバードは既に 起動 しています！").toString());
                }else {
                    sender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append("フラッピーバードを 起動 しました").toString());
                    flappybird.setActive(true);
                }
                break;
            case "inactivate":
                if (!flappybird.isActive()) {
                    sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("フラッピーバードは既に 停止 しています！").toString());
                } else {
                    sender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append("フラッピーバードを 停止 しました").toString());
                    flappybird.setActive(false);
                }
                break;
            case "status":
                sender.sendMessage(new StringBuilder()
                        .append(String.format("状態: %s", flappybird.isActive() ? ChatColor.GREEN + "起動中" : ChatColor.RED + "停止中")).append("\n")
                        .toString());
                break;
            case "config":
                config(sender, command, label, args);
                break;
            default:
                sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("無効な引数です！").toString());
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        List<String> suggestions = null;

        if (args.length == 1) {
            suggestions = new ArrayList<>(Arrays.asList("activate", "inactivate", "config", "status", "join", "leave")).stream().filter(s -> s.contains(args[0])).collect(Collectors.toList());
        } else if (args.length == 2) {
            switch (args[0]) {
                case "config":
                    suggestions = new ArrayList<>(Arrays.asList("set", "reset", "reload", "save", "get")).stream().filter(s -> s.contains(args[1])).collect(Collectors.toList());
                    break;
                case "join":
                case "leave":
                    suggestions = Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).filter(s -> s.contains(args[1])).collect(Collectors.toList());
                    suggestions.addAll(new ArrayList<>(Arrays.asList("all", "@a", "@p", "@r", "@s")));
                    suggestions.stream().filter(s -> s.contains(args[1])).collect(Collectors.toList());
                    break;
            }
        } else if(args.length == 3) {
            if (args[0].equals("config") && ((args[1].equals("set")) || (args[1].equals("get")))) {
                suggestions = flappybird.getConfig().getValues(false).keySet().stream().filter(s -> s.contains(args[2])).collect(Collectors.toList());
            }
        }

        return suggestions;
    }

    private void config(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        if (args.length == 1) {
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("引数が足りません！").toString());
            return;
        }
        switch (args[1]) {
            case "set":
                setConfig(sender, command, alias, args);
                break;
            case "get":
                getConfig(sender, command, alias, args);
                break;
            case "reset":
                File file = new File(flappybird.getDataFolder(), "config.yml");
                file.delete();
                flappybird.saveDefaultConfig();
                flappybird.reloadConfig();
                sender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append("設定ファイルを再生成しました").toString());
                break;
            case "reload":
                flappybird.reloadConfig();
                sender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(String.format("設定ファイルを再読み込みしました")).toString());
                break;
            case "save":
                flappybird.saveConfig();
                sender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(String.format("設定ファイルを保存しました")).toString());
                break;
            default:
                sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("無効な引数です！").toString());
                break;
        }
    }

    private void setConfig(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("引数が足りません！").toString());
            return;
        }
        String key = args[2];
        switch (key) {
            case "distance":
            case "forceJump":
            case "jumpMax":
            case "jumpMin":
            case "x":
            case "z":
            case "forward":
            case "right":
            case "ratio":
            case "pitchRatio":
            case "tutorialTick":
            case "collisionTick":
            case "noCollisionTick":
                double value;
                try {
                    value = Double.parseDouble(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("第３引数は 数 にしてください！").toString());
                    return;
                }
                flappybird.getConfig().set(key, value);
                sender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(String.format("%s を %s に設定しました", key, value)).toString());
                break;
            case "kill":
            case "partilce":
            case "jumpGameOnly":
            case "forceSpectator":
            case "training":
                boolean bool;
                if (!(args[3].equals("true") || args[3].equals("false"))) {
                    sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("第３引数は ブール型 にしてください！").toString());
                    return;
                }
                bool = Boolean.parseBoolean(args[3]);
                flappybird.getConfig().set(key, bool);
                sender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(String.format("%s を %s に設定しました", key, bool)).toString());
                break;
            default:
                sender.sendMessage(new StringBuilder().append(ChatColor.RED).append(String.format("%s という設定項目は存在しません！", key)).toString());
                break;
        }
    }

    private void getConfig(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("引数が足りません！").toString());
            return;
        }
        String key = args[2];
        if (!flappybird.getConfig().getValues(false).keySet().contains(key)) {
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append(String.format("%s という設定項目は存在しません！", key)).toString());
            return;
        }
        String value = flappybird.getConfig().get(key).toString();
        sender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(String.format("%s の値は %s です", key, value)).toString());
    }

    private void join(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        if (args.length == 1 && sender instanceof Player) {
            boolean result = flappybird.join((Player) sender);
            String message = result ? ChatColor.GREEN + "ゲームに参加しました" : ChatColor.RED + "既に参加しています！";
            sender.sendMessage(message);
            return;
        } else if (args.length == 1 && !(sender instanceof Player)) {
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("引数が足りません！").toString());
            return;
        }
        if (args[1].equals("all")) {
            flappybird.allStart();
            return;
        }
        List<Player> players = Bukkit.selectEntities(sender, args[1]).stream().filter(entity -> entity instanceof Player).map(entity -> (Player) entity).collect(Collectors.toList());
        if (players.isEmpty()) {
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("対象が見つかりません！").toString());
            return;
        }
        players.forEach(player -> flappybird.join(player));
        sender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(players.stream().map(player -> player.getName()).collect(Collectors.toList()).toString()).append(" をゲームに参加させました！").toString());
        return;
    }

    private void leave(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        if (args.length == 1 && sender instanceof Player) {
            boolean result = flappybird.leave((Player) sender);
            String message = result ? ChatColor.GREEN + "ゲームから退出しました" : ChatColor.RED + "ゲームに参加していません！";
            sender.sendMessage(message);
            return;
        } else if (args.length == 1 && !(sender instanceof Player)) {
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("引数が足りません！").toString());
            return;
        }
        if (args[1].equals("all")) {
            flappybird.allStop();
            return;
        }
        List<Player> players = Bukkit.selectEntities(sender, args[1]).stream().filter(entity -> entity instanceof Player).map(entity -> (Player) entity).collect(Collectors.toList());
        if (players.isEmpty()) {
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("対象が見つかりません！").toString());
            return;
        }
        players.forEach(player -> flappybird.leave(player));
        sender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(players.stream().map(player -> player.getName()).collect(Collectors.toList()).toString()).append(" をゲームから退出させました").toString());
        return;
    }
}
