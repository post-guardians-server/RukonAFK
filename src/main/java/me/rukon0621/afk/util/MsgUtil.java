package me.rukon0621.afk.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MsgUtil {
    private static final String pfix = "&7[ &c! &7] ";

    public static void send(CommandSender sender, String message) {
        send(sender, message, pfix);
    }

    public static void send(CommandSender sender, String message, String prefix) {
        if(sender instanceof Player player) {
            MsgUtil.send(player, message, prefix);
        }
        else {
            System.out.println(color(message));
        }
    }

    public static void send(Player player, String message, String prefix) {
        player.sendMessage(MsgUtil.color(prefix + message));
    }

    public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

}
