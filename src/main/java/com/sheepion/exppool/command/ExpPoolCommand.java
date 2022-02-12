package com.sheepion.exppool.command;

import com.sheepion.exppool.ExpPool;
import com.sheepion.exppool.PoolManager;
import com.sheepion.exppool.listener.PointSelect;
import com.sheepion.exppool.listener.Pool;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExpPoolCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }
        switch (args[0]) {
            case "add" -> {
                addExpPool((Player) sender, args);
                return true;
            }
            case "reload" -> {
                reload(sender);
                return true;
            }
        }
        return false;
    }

    public void reload(CommandSender sender) {
        if (sender.isOp() || sender.hasPermission("exppool.reload")) {
            PoolManager.reload();
        }
    }

    //用法/exppool add <name> <exp> <join> <leave>
    public void addExpPool(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "参数不足");
            return;
        }
        if (!player.isOp() || !player.hasPermission("exppool.add")) {
            return;
        }
        String join=null;
        String leave=null;
        if(args.length==4){
            join=args[3];
        }
        if(args.length==5){
            leave=args[4];
        }
        if (PointSelect.point1Select.containsKey(player.getUniqueId())
                && PointSelect.point2Select.containsKey(player.getUniqueId())) {
            Location p1 = PointSelect.point1Select.get(player.getUniqueId());
            Location p2 = PointSelect.point2Select.get(player.getUniqueId());
            String world = p1.getWorld().getName();
            ExpPool.plugin.getConfig().set("pools." + world + "." + args[1] + ".exp", Integer.parseInt(args[2]));
            ExpPool.plugin.getConfig().set("pools." + world + "." + args[1] + ".x1", p1.getBlockX());
            ExpPool.plugin.getConfig().set("pools." + world + "." + args[1] + ".y1", p1.getBlockY());
            ExpPool.plugin.getConfig().set("pools." + world + "." + args[1] + ".z1", p1.getBlockZ());
            ExpPool.plugin.getConfig().set("pools." + world + "." + args[1] + ".x2", p2.getBlockX());
            ExpPool.plugin.getConfig().set("pools." + world + "." + args[1] + ".y2", p2.getBlockY());
            ExpPool.plugin.getConfig().set("pools." + world + "." + args[1] + ".z2", p2.getBlockZ());
            ExpPool.plugin.getConfig().set("pools." + world + "." + args[1] + ".join", join);
            ExpPool.plugin.getConfig().set("pools." + world + "." + args[1] + ".leave", leave);
            ExpPool.plugin.saveConfig();
            PointSelect.point1Select.remove(player.getUniqueId());
            PointSelect.point2Select.remove(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "添加成功");
            PoolManager.pools.add(new Pool(world
                    , p1.getBlockX(), p1.getBlockY(), p1.getBlockZ()
                    , p2.getBlockX(), p2.getBlockY(), p2.getBlockZ()
                    , Integer.parseInt(args[2])
                    , args[3], args[4]));
        }
    }
}
