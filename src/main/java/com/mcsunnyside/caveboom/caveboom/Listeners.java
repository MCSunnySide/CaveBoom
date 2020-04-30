package com.mcsunnyside.caveboom.caveboom;

import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@AllArgsConstructor
public class Listeners implements Listener {
    private final CaveBoom plugin;
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private final int chance;
    private final List<Material> materials;
    private final Scheduler scheduler;
    private final List<String> worlds;


    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlockReplacedState().getType() != Material.CAVE_AIR && event.getBlockReplacedState().getType() != Material.VOID_AIR) {
            return;
        }
        if (!materials.contains(event.getBlockPlaced().getType())) {
            return;
        }
        if (random.nextInt(chance) != 0) {
            return;
        }
        if(event.getPlayer().hasPermission("caveboom.bypass")){
            plugin.getLogger().info("玩家 "+event.getPlayer().getName()+" 拥有caveboom.bypass权限，取消爆炸");
            return;
        }
        if(!worlds.contains(event.getBlockPlaced().getLocation().getWorld().getName())){
            return;
        }
        //开始制造爆炸
        scheduler.add(event.getBlockReplacedState().getLocation());
        plugin.getLogger().info("产生爆炸：" + event.getBlockReplacedState().getLocation() + " 触发玩家：" + event.getPlayer().getName());
    }
}
