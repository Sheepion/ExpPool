package com.sheepion.exppool.listener;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.UUID;

public class PointSelect implements Listener {
    public static final HashMap<UUID, Location> point1Select = new HashMap<>();
    public static final HashMap<UUID, Location> point2Select = new HashMap<>();

    //监听op使用附魔之瓶选择方块
    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!player.isOp()) {
            return;
        }
        if (!player.getInventory().getItemInMainHand().getType().equals(Material.EXPERIENCE_BOTTLE)) {
            return;
        }
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }
        assert event.getClickedBlock() != null;
        Location location = event.getClickedBlock().getLocation();
        //已经选择点2，清除点，重新添加点1
        if (point2Select.containsKey(player.getUniqueId())) {
            point2Select.remove(player.getUniqueId());
            point1Select.put(player.getUniqueId(), location);
            player.sendMessage(ChatColor.GREEN + "选择点1: x:" + location.getBlockX() + " y:" + location.getBlockY() + " z:" + location.getBlockZ());
        }
        //已经选择点1，添加点2
        else if (point1Select.containsKey(player.getUniqueId())) {
            point2Select.put(player.getUniqueId(), location);
            player.sendMessage(ChatColor.GREEN + "选择点2: x:" + location.getBlockX() + " y:" + location.getBlockY() + " z:" + location.getBlockZ());
        }
        //没有选择点，添加点1
        else {
            point1Select.put(player.getUniqueId(), location);
            player.sendMessage(ChatColor.GREEN + "选择点1: x:" + location.getBlockX() + " y:" + location.getBlockY() + " z:" + location.getBlockZ());
        }
        event.setCancelled(true);
    }

    //切换世界以后清除选择的点
    @EventHandler(ignoreCancelled = true)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        point1Select.remove(event.getPlayer().getUniqueId());
        point2Select.remove(event.getPlayer().getUniqueId());
    }
}
