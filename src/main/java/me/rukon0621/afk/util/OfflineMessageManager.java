package me.rukon0621.afk.util;

import me.rukon0621.afk.RukonAFK;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class OfflineMessageManager {

    /**
     *
     * @param player player
     * @param message message
     */
    @Deprecated
    public static void sendOfflineMessage(OfflinePlayer player, String message) {
        ArrayList<String> messages = new ArrayList<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                getMessages(player, messages);
                messages.add(message);
                setMessages(player, messages);
            }
        }.runTaskAsynchronously(RukonAFK.inst());
    }

    public static void sendOfflineMessage(String uuid, String message) {
        ArrayList<String> messages = new ArrayList<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                getMessages(uuid, messages);
                messages.add(message);
                setMessages(uuid, messages);
            }
        }.runTaskAsynchronously(RukonAFK.inst());
    }

    @Deprecated
    public static void setMessages(OfflinePlayer player, ArrayList<String> messages) {
        DataBase db = new DataBase();
        try {
            PreparedStatement statement = db.getConnection().prepareStatement(String.format("UPDATE playerData SET offlineMessages = ? WHERE uuid = '%s'", player.getUniqueId()));
            statement.setBytes(1, Serializer.serialize(messages));
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
    }

    public static void setMessages(String uuid, ArrayList<String> messages) {
        DataBase db = new DataBase();
        try {
            PreparedStatement statement = db.getConnection().prepareStatement(String.format("UPDATE playerData SET offlineMessages = ? WHERE uuid = '%s'", uuid));
            statement.setBytes(1, Serializer.serialize(messages));
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
    }

    public static void getMessages(String uuid, ArrayList<String> messages) {
        messages.clear();
        DataBase db = new DataBase();
        ResultSet set = db.executeQuery(String.format("SELECT offlineMessages FROM playerData WHERE uuid = '%s'", uuid));
        try {
            set.next();
            messages.addAll((ArrayList<String>) Serializer.deserialize(set.getBytes(1)));
            set.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            messages.clear();
        }
        db.close();
    }

    @Deprecated
    public static void getMessages(OfflinePlayer player, ArrayList<String> messages) {
        messages.clear();
        DataBase db = new DataBase();
        ResultSet set = db.executeQuery(String.format("SELECT offlineMessages FROM playerData WHERE uuid = '%s'", player.getUniqueId()));
        try {
            set.next();
            messages.addAll((ArrayList<String>) NullManager.defaultNull(Serializer.deserialize(set.getBytes(1)), new ArrayList<>()));
            set.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            messages.clear();
        }
        db.close();
    }

    @Deprecated
    public static ArrayList<String> getMessages(OfflinePlayer player) {
        ArrayList<String> messages = new ArrayList<>();
        DataBase db = new DataBase();
        ResultSet set = db.executeQuery(String.format("SELECT offlineMessages FROM playerData WHERE uuid = '%s'", player.getUniqueId()));
        try {
            set.next();
            messages = (ArrayList<String>) Serializer.deserialize(set.getBytes(1));
            set.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
        if(messages==null) messages = new ArrayList<>();
        return messages;
    }

}
