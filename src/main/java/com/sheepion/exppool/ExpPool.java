package com.sheepion.exppool;

import com.sheepion.exppool.command.ExpPoolCommand;
import com.sheepion.exppool.listener.EnterPoolListener;
import com.sheepion.exppool.listener.PointSelect;
import org.bukkit.plugin.java.JavaPlugin;

public final class ExpPool extends JavaPlugin {
    public static JavaPlugin plugin;
    public ExpPool(){
        plugin = this;
    }
    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new PointSelect(), this);
        getServer().getPluginManager().registerEvents(new EnterPoolListener(), this);
        getCommand("exppool").setExecutor(new ExpPoolCommand());
        PoolManager.reload();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
