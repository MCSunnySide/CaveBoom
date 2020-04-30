package com.mcsunnyside.caveboom.caveboom;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class CaveBoom extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        reloadConfig();
        List<Material> materialList = new ArrayList<>();
        for (String string : getConfig().getStringList("materials")) {
            string = string.toUpperCase();
            Material material = Material.matchMaterial(string);
            if (material == null) {
                getLogger().info("Invaild material: " + string);
                continue;
            }
            materialList.add(material);
        }

        Bukkit.getPluginManager().registerEvents(
                new Listeners(this, getConfig().getInt("chance.trigger")
                        , materialList
                        , new Scheduler(this, getConfig().getInt("depth")
                        , getConfig().getInt("chance.explode")
                        , (float) getConfig().getDouble("power", 4.0d)
                        , getConfig().getBoolean("fire")
                        , getConfig().getBoolean("breakblocks"))
                        , getConfig().getStringList("worlds"))
                , this); //ok
        getLogger().info("插件启动完毕");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
