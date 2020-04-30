package com.mcsunnyside.caveboom.caveboom;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Scheduler {
    private final Queue<Location> queue = new LinkedList<>();
    private final ObjectLinkedOpenHashSet<Location> caches = new ObjectLinkedOpenHashSet<>();
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private final int depthLimits;
    private CaveBoom plugin;

    public Scheduler(CaveBoom plugin, int depthLimits, int chance, float power, boolean fire, boolean breakBlocks) {
        this.depthLimits = depthLimits;

        plugin.getLogger().info(chance + "");
        new BukkitRunnable() {
            @Override
            public void run() {
                Location location = queue.poll();
                while (location != null) {
                    scan(location, 0, null);
                    location = queue.poll();
                }
                caches.forEach(loc -> {
                    if (random.nextInt(chance) == 0) {
                        loc.getWorld().createExplosion(loc, power, fire, breakBlocks);
                    }
                });
                caches.clear();
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    @NotNull //搜索周围的方块
    private List<Location> scan(@NotNull Location location, int depth, @Nullable BlockFace blockFaceFrom) {
        if (depth > depthLimits) {
            return Lists.newArrayList();
        }
        depth++;

        Block block = location.getBlock();
        Map<Location, BlockFace> blocks = new HashMap<>();
        if (blockFaceFrom == null) {
            blocks.put(block.getRelative(BlockFace.UP).getLocation(), BlockFace.UP);
            blocks.put(block.getRelative(BlockFace.DOWN).getLocation(), BlockFace.DOWN);
            blocks.put(block.getRelative(BlockFace.NORTH).getLocation(), BlockFace.NORTH);
            blocks.put(block.getRelative(BlockFace.EAST).getLocation(), BlockFace.EAST);
            blocks.put(block.getRelative(BlockFace.SOUTH).getLocation(), BlockFace.SOUTH);
            blocks.put(block.getRelative(BlockFace.WEST).getLocation(), BlockFace.WEST);
        } else {
            if (blockFaceFrom != BlockFace.DOWN) {
                blocks.put(block.getRelative(BlockFace.UP).getLocation(), BlockFace.UP);
            }
            if (blockFaceFrom != BlockFace.UP) {
                blocks.put(block.getRelative(BlockFace.DOWN).getLocation(), BlockFace.DOWN);
            }
            if (blockFaceFrom != BlockFace.SOUTH) {
                blocks.put(block.getRelative(BlockFace.NORTH).getLocation(), BlockFace.NORTH);
            }
            if (blockFaceFrom != BlockFace.WEST) {
                blocks.put(block.getRelative(BlockFace.EAST).getLocation(), BlockFace.EAST);
            }
            if (blockFaceFrom != BlockFace.NORTH) {
                blocks.put(block.getRelative(BlockFace.SOUTH).getLocation(), BlockFace.SOUTH);
            }
            if (blockFaceFrom != BlockFace.EAST) {
                blocks.put(block.getRelative(BlockFace.WEST).getLocation(), BlockFace.WEST);
            }
        }

        List<Location> returns = new ArrayList<>();

        for (Map.Entry<Location, BlockFace> entry : blocks.entrySet()) {
            if (!caches.add(entry.getKey())) {
                continue;
            }
            returns.addAll(scan(entry.getKey(), depth, entry.getValue()));
        }

        return returns;
    }

    public void add(@NotNull Location location) {
        this.queue.add(location);
    }

}
