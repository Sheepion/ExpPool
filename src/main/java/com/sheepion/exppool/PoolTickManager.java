package com.sheepion.exppool;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Calendar;

import static com.sheepion.exppool.PoolManager.setUsedTicks;

public class PoolTickManager implements Runnable {
    private final Player player;
    public PoolTickManager(Player player) {
        this.player = player;
    }
    @Override
    public void run() {
        Calendar calendar = Calendar.getInstance();
        //当前月份
        int month=calendar.get(Calendar.MONTH)+1;
        //当前日期
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        //在pdc中使用date来存取当前日期，用来更新每日的ticks。
        //date=month*100+day
        PersistentDataContainer pdc=player.getPersistentDataContainer();
        //已经记录过日期
        if(pdc.has(new NamespacedKey(ExpPool.plugin,"date"), PersistentDataType.INTEGER)){
            int date=pdc.get(new NamespacedKey(ExpPool.plugin,"date"), PersistentDataType.INTEGER);
            //如果当前日期和上次日期不同，则更新ticks
            if(date!=month*100+day){
                pdc.set(new NamespacedKey(ExpPool.plugin,"date"),PersistentDataType.INTEGER,month*100+day);
                setUsedTicks(player,0);
            }
        }
        //没有记录过日期，添加当前日期
        else{
            pdc.set(new NamespacedKey(ExpPool.plugin,"date"),PersistentDataType.INTEGER,month*100+day);
            setUsedTicks(player,0);
        }
    }
}
