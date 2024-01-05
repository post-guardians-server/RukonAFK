package me.rukon0621.afk;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class EventListener implements Listener {

    public EventListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, RukonAFK.inst());
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(player.getLocation().getY() > -40) continue;
                    player.teleport(player.getWorld().getSpawnLocation());
                }
            }
        }.runTaskTimer(RukonAFK.inst(), 100, 100);
    }

    @EventHandler
    public void onDamaged(EntityDamageEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onChat(AsyncChatEvent e) {
        if(e.getPlayer().isOp()) return;
        e.setCancelled(true);
    }
}
