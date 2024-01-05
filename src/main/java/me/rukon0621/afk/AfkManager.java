package me.rukon0621.afk;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import me.rukon0621.afk.util.DataBase;
import me.rukon0621.afk.util.DateUtil;
import me.rukon0621.afk.util.MsgUtil;
import me.rukon0621.afk.util.OfflineMessageManager;
import me.rukon0621.callback.ProxyCallBack;
import me.rukon0621.callback.ProxySender;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class AfkManager implements Listener {
    private final RukonAFK plugin = RukonAFK.inst();
    private final Location afkLoc;
    private final Map<UUID, Long> onAfk = new HashMap<>();

    private static final int MAX_POINT = 720;
    private static final int POINT_PER_SECOND = 30;

    public AfkManager() {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        afkLoc = new Location(Bukkit.getWorld("world"), 0, 0, 0, 45, 7);
    }

    public void startAfk(Player player) {
        onAfk.put(player.getUniqueId(), System.currentTimeMillis());
    }
    public void stopAfk(Player player) {
        int point = Math.min((int) (getAfkSecond(player) / POINT_PER_SECOND), MAX_POINT);
        DataBase db = new DataBase();
        String whereStr = String.format("WHERE uuid = '%s'", player.getUniqueId());
        try {
            ResultSet set = db.executeQuery("SELECT afkPoint from playerData " + whereStr);
            set.next();
            point += set.getInt(1);
            set.close();
            PreparedStatement statement = db.getConnection().prepareStatement("UPDATE playerData SET afkPoint = ? " + whereStr);
            statement.setInt(1, point);
            statement.executeUpdate();
            statement.close();
            String str = String.format("&7[ &c! &7] &e%s &f동안 잠수를 진행하여 &e%d &f만큼의 잠수 포인트를 획득했습니다.", DateUtil.formatDate(getAfkSecond(player)), Math.min((int) (getAfkSecond(player) / POINT_PER_SECOND), MAX_POINT));
            OfflineMessageManager.sendOfflineMessage(player.getUniqueId().toString(), str);

            System.out.println(player.getName() + " - " + str);

        } catch (Exception e) {
            e.printStackTrace();
        }
        onAfk.remove(player.getUniqueId());
    }

    public long getAfkSecond(Player player) {
        return (System.currentTimeMillis() - onAfk.get(player.getUniqueId())) / 1000L;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        e.setJoinMessage(null);
        for(Player lp : Bukkit.getOnlinePlayers()) {
            player.hidePlayer(plugin, lp);
            lp.hidePlayer(plugin, player);
        }
        player.teleport(afkLoc);
        player.setGameMode(GameMode.SPECTATOR);
        player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1.5f);
        MsgUtil.send(player, "┌──────────────────────────────┐", " ");
        MsgUtil.send(player, " ", " ");
        MsgUtil.send(player, "&c주의: 움직이면 잠수가 해제됩니다.");
        MsgUtil.send(player, " ", " ");
        MsgUtil.send(player, "&f잠수를 중단하면 &b메인 서버&f로 돌아가지만 메인 서버가 &c가득 찼을 경우 대기열&f로 이동됩니다.");
        MsgUtil.send(player, " ", " ");
        MsgUtil.send(player, "└──────────────────────────────┘", " ");
        if(onAfk.containsKey(player.getUniqueId())) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                if(!player.isOnline()) return;
                startAfk(player);
            }
        }.runTaskLater(RukonAFK.inst(), 20);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if(e.getFrom().distanceSquared(e.getTo()) <= 0) return;
        if(!onAfk.containsKey(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            return;
        }
        stopAfk(e.getPlayer());
        new ProxyCallBack(e.getPlayer(), "stopAfk") {
            @Override
            protected void constructExtraByteData(ByteArrayDataOutput byteArrayDataOutput) {}

            @Override
            public void done(ByteArrayDataInput in) {
                if(!in.readUTF().equals("failConnection")) return;
                player.kickPlayer(MsgUtil.color("&e대기열 서버에 접속하지 못했습니다. 서버 접속을 다시 시도해주세요."));
            }
        };
        new BukkitRunnable() {
            @Override
            public void run() {
                if(e.getPlayer().isOnline()) e.getPlayer().kickPlayer(MsgUtil.color("&c대기열 서버가 닫혀있어 서버에서 나가졌습니다."));
            }
        }.runTaskLater(plugin, 40);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        if(!onAfk.containsKey(e.getPlayer().getUniqueId())) return;
        stopAfk(e.getPlayer());
    }
}
