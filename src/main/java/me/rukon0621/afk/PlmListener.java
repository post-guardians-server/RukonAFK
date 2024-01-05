package me.rukon0621.afk;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class PlmListener implements PluginMessageListener {

    private static final String MAIN_CHANNEL = "postgz:channel";

    public PlmListener() {
        Bukkit.getMessenger().registerIncomingPluginChannel(RukonAFK.inst(), MAIN_CHANNEL, this);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String s, @NotNull Player player, @NotNull byte[] bytes) {
        if(!s.equals(MAIN_CHANNEL)) return;
        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        String subChannel = in.readUTF();
        if(subChannel.equals("command")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), in.readUTF());
        }
    }
}
