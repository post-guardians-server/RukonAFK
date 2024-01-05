package me.rukon0621.afk;

import org.bukkit.plugin.java.JavaPlugin;

public class RukonAFK extends JavaPlugin {
    private static RukonAFK inst;
    public static final String mainDB = "guardians";
    public static RukonAFK inst() {
        return inst;
    }

    @Override
    public void onEnable() {
        inst = this;
        getServer().getPluginsFolder().mkdir();
        new AfkManager();
        new EventListener();
        new PlmListener();
    }

    @Override
    public void onDisable() {
    }

}
