package dev.sonmiike.smcore;

import dev.sonmiike.smcore.core.commands.Registration;
import dev.sonmiike.smcore.core.listeners.*;
import dev.sonmiike.smcore.core.managers.NPCManager;
import dev.sonmiike.smcore.core.managers.TaskManager;
import dev.sonmiike.smcore.core.managers.TeamsManager;
import dev.sonmiike.smcore.core.managers.VanishManager;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.N;

@SuppressWarnings("UnstableApiUsage")
public final class SmCore extends JavaPlugin {

    private JavaPlugin plugin;
    private TaskManager taskManager;
    private TeamsManager teamsManager;
    private VanishManager vanishManager;
    private NPCManager npcManager;
    private LuckPerms luckPerms;

    @Override
    public void onEnable() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider(); }

        plugin = this;
        taskManager = new TaskManager();
        teamsManager = new TeamsManager();
        vanishManager = new VanishManager(this, taskManager);
        npcManager = new NPCManager();




        registerListeners();

        // Commands Registration
        Registration.registerViaOnEnable(this);

    }



    @Override
    public void onDisable() {
    }



    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(vanishManager, teamsManager), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        getServer().getPluginManager().registerEvents(new AsyncPlayerChatListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractEventListener(), this);
        new LuckPermsListener(this, teamsManager, vanishManager, luckPerms);
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public NPCManager getNpcManager() {
        return npcManager;
    }

}
