package dev.sonmiike.smcore;

import dev.sonmiike.smcore.core.commands.GameModeCommand;
import dev.sonmiike.smcore.core.commands.NPCCommand;
import dev.sonmiike.smcore.core.commands.VanishCommand;
import dev.sonmiike.smcore.core.listeners.*;
import dev.sonmiike.smcore.core.managers.NPCManager;
import dev.sonmiike.smcore.core.managers.TaskManager;
import dev.sonmiike.smcore.core.managers.TeamsManager;
import dev.sonmiike.smcore.core.managers.VanishManager;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("UnstableApiUsage")
public final class SmCore extends JavaPlugin {


    private JavaPlugin plugin;
    private TeamsManager teamsManager;
    private VanishManager vanishManager;
    @Getter
    private NPCManager npcManager;
    private TaskManager taskManager;
    private LuckPerms luckPerms;

    @Override
    public void onEnable() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider(); }

        plugin = this;
        taskManager = new TaskManager();
        teamsManager = new TeamsManager();
        vanishManager = new VanishManager(this, taskManager, teamsManager);
        npcManager = new NPCManager(this);

        registerListeners();
        registerViaOnEnable(this);


    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(vanishManager, teamsManager, npcManager), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        getServer().getPluginManager().registerEvents(new AsyncPlayerChatListener(), this);
//        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractEventListener(), this);
        new LuckPermsListener(this, teamsManager, vanishManager, luckPerms);
    }

    public  void registerViaOnEnable(final SmCore plugin) {
        registerViaLifecycleEvents(plugin);
    }

    private void registerViaLifecycleEvents(final SmCore plugin) {
        final LifecycleEventManager<Plugin> lifecycleManager = plugin.getLifecycleManager();
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            NPCManager npcManager = plugin.getNpcManager();


            new GameModeCommand(plugin, commands);
            new NPCCommand(plugin, commands, npcManager);
            new VanishCommand(plugin, commands, this.vanishManager);

        });
    }


}
