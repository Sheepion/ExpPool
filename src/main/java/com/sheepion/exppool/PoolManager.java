package com.sheepion.exppool;

import com.sheepion.exppool.listener.Pool;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.HashSet;

public class PoolManager implements Runnable {
    private final Player player;
    public static final HashSet<Pool> pools = new HashSet<>();
    public static final HashMap<String, Long> maxTicks = new HashMap<>();

    public static void reload() {
        //重载pools
        pools.clear();
        ExpPool.plugin.reloadConfig();
        ConfigurationSection poolSection = ExpPool.plugin.getConfig().getConfigurationSection("pools");
        for (String worldName : poolSection.getKeys(false)) {
            ConfigurationSection worldSection = poolSection.getConfigurationSection(worldName);
            for (String poolName : worldSection.getKeys(false)) {
                Pool pool = new Pool(worldName
                        , worldSection.getInt(poolName + ".x1")
                        , worldSection.getInt(poolName + ".y1")
                        , worldSection.getInt(poolName + ".z1")
                        , worldSection.getInt(poolName + ".x2")
                        , worldSection.getInt(poolName + ".y2")
                        , worldSection.getInt(poolName + ".z2")
                        , worldSection.getInt(poolName + ".exp"));
                pools.add(pool);
            }
        }
        //重载权限
        maxTicks.clear();
        ConfigurationSection maxTickSection = ExpPool.plugin.getConfig().getConfigurationSection("max-ticks");
        for (String permission : maxTickSection.getKeys(false)) {
            maxTicks.put(permission, maxTickSection.getLong(permission));
        }
        ExpPool.plugin.getLogger().info("重载经验池成功");
    }

    /**
     * 获取player能在经验池中最多待多少ticks
     *
     * @param player 玩家
     */
    public static long getMaxTicks(Player player) {
        long result = 0L;
        for (String permission : maxTicks.keySet()) {
            if(player.hasPermission("exppool.maxtick." + permission)) {
                result = Math.max(result, maxTicks.get(permission));
            }
        }
        return result;
    }

    /**
     * 获取player今天已经在经验池中待了多少tick
     *
     * @param player 玩家
     * @return 已经使用的tick数量
     */
    public static long getUsedTicks(Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        if (pdc.has(new NamespacedKey(ExpPool.plugin, "used-ticks"), PersistentDataType.LONG)) {
            return pdc.get(new NamespacedKey(ExpPool.plugin, "used-ticks"), PersistentDataType.LONG);
        } else {
            pdc.set(new NamespacedKey(ExpPool.plugin, "used-ticks"), PersistentDataType.LONG, 0L);
        }
        return 0L;
    }

    /**
     * 给玩家增加已经使用的经验池tick
     *
     * @param player 玩家
     * @param ticks  增加的tick数量
     */
    public static void addUsedTicks(Player player, long ticks) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        if (pdc.has(new NamespacedKey(ExpPool.plugin, "used-ticks"), PersistentDataType.LONG)) {
            pdc.set(new NamespacedKey(ExpPool.plugin, "used-ticks"), PersistentDataType.LONG, getUsedTicks(player) + ticks);
        } else {
            pdc.set(new NamespacedKey(ExpPool.plugin, "used-ticks"), PersistentDataType.LONG, ticks);
        }
    }

    /**
     * 修改玩家当前已经使用的经验池tick
     *
     * @param player 玩家
     * @param ticks  增加的tick数量
     */
    public static void setUsedTicks(Player player, long ticks) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        if (pdc.has(new NamespacedKey(ExpPool.plugin, "used-ticks"), PersistentDataType.LONG)) {
            pdc.set(new NamespacedKey(ExpPool.plugin, "used-ticks"), PersistentDataType.LONG, ticks);
        } else {
            pdc.set(new NamespacedKey(ExpPool.plugin, "used-ticks"), PersistentDataType.LONG, ticks);
        }
    }


    /**
     * 返回玩家目前所在的经验池
     *
     * @param player 玩家
     * @return 经验池，如果没有则返回null
     */
    public static Pool isInPool(Player player) {
        Location location = player.getLocation();
        for (Pool pool : pools) {
            if (location.getWorld().getName().equals(pool.worldName())) {
                int minX = Math.min(pool.x1(), pool.x2());
                int maxX = Math.max(pool.x1(), pool.x2());
                if (location.getBlockX() < minX || location.getBlockX() > maxX) {
                    continue;
                }
                int minY = Math.min(pool.y1(), pool.y2());
                int maxY = Math.max(pool.y1(), pool.y2());
                if (location.getBlockY() < minY || location.getBlockY() > maxY) {
                    continue;
                }
                int minZ = Math.min(pool.z1(), pool.z2());
                int maxZ = Math.max(pool.z1(), pool.z2());
                if (location.getBlockZ() < minZ || location.getBlockZ() > maxZ) {
                    continue;
                }
                return pool;
            }
        }
        return null;
    }

    public PoolManager(Player player) {
        this.player = player;
    }

    //给正在经验池内且没有超出tick上限的玩家产生经验
    @Override
    public void run() {
        //判断是否在经验池内
        Pool pool = isInPool(player);
        if (pool == null) {
            return;
        }
        //判断是否已经超出最大ticks
        if (getUsedTicks(player) >= getMaxTicks(player)) {
            return;
        }
        player.sendMessage("经验池信息：" + pool.toString());
        //给玩家加上已使用的ticks
        addUsedTicks(player, ExpPool.plugin.getConfig().getLong("interval"));
        //生成经验
        ((ExperienceOrb) player.getWorld().spawnEntity(player.getLocation(), EntityType.EXPERIENCE_ORB)).setExperience(pool.exp());
    }
}

