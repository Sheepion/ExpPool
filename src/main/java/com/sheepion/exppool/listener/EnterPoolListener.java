package com.sheepion.exppool.listener;

import com.sheepion.exppool.ExpPool;
import com.sheepion.exppool.PoolManager;
import com.sheepion.exppool.PoolTickManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class EnterPoolListener implements Listener {
    //判断玩家是否在经验池并且给玩家经验的任务
    private static final HashMap<UUID, BukkitTask> poolTaskMap = new HashMap<>();
    //用来判断玩家已用的ticks要不要刷新（判断是否已经到第二天）
    private static final HashMap<UUID, BukkitTask> tickUpdateTaskMap = new HashMap<>();

    public EnterPoolListener() {
        reload();
    }

    public static void reload(){
        //关闭原有的任务
        for (UUID uuid : poolTaskMap.keySet()) {
            poolTaskMap.get(uuid).cancel();
        }
        poolTaskMap.clear();
        for (UUID uuid : tickUpdateTaskMap.keySet()) {
            tickUpdateTaskMap.get(uuid).cancel();
        }
        tickUpdateTaskMap.clear();
        //重新加载任务
        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            BukkitTask poolTask = ExpPool.plugin.getServer().getScheduler().runTaskTimer(
                    ExpPool.plugin, new PoolManager(onlinePlayer), 0L, ExpPool.plugin.getConfig().getLong("interval"));
            BukkitTask tickUpdateTask = ExpPool.plugin.getServer().getScheduler().runTaskTimer(
                    ExpPool.plugin, new PoolTickManager(onlinePlayer), 600L, 1200L);
            poolTaskMap.put(onlinePlayer.getUniqueId(), poolTask);
            tickUpdateTaskMap.put(onlinePlayer.getUniqueId(), tickUpdateTask);
        }
    }
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        BukkitTask poolTask = ExpPool.plugin.getServer().getScheduler().runTaskTimer(
                ExpPool.plugin, new PoolManager(player), 0L, ExpPool.plugin.getConfig().getLong("interval"));
        BukkitTask tickUpdateTask = ExpPool.plugin.getServer().getScheduler().runTaskTimer(
                ExpPool.plugin, new PoolTickManager(player), 600L, 1200L);
        poolTaskMap.put(player.getUniqueId(), poolTask);
        tickUpdateTaskMap.put(player.getUniqueId(), tickUpdateTask);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        poolTaskMap.get(event.getPlayer().getUniqueId()).cancel();
        poolTaskMap.remove(event.getPlayer().getUniqueId());
        tickUpdateTaskMap.get(event.getPlayer().getUniqueId()).cancel();
        tickUpdateTaskMap.remove(event.getPlayer().getUniqueId());
    }
}
