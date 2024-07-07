package dev.sonmiike.smcore;

import dev.sonmiike.smcore.core.commands.*;
import dev.sonmiike.smcore.core.listeners.*;
import dev.sonmiike.smcore.core.managers.*;
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
public final class SmCore extends JavaPlugin
{

    //instance
    private JavaPlugin plugin;

    // Managers
    private TeamsManager teamsManager;
    private VanishManager vanishManager;
    private GodManager godManager;
    @Getter private NPCManager npcManager;
    private MuteManager muteManager;
    private TaskManager taskManager;
    private LuckPerms luckPerms;

    // Database
    @Getter private DatabaseManager databaseManager;

    @Override
    public void onEnable()
    {
        saveDefaultConfig();

        connectToDatabase();
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null)
        {
            luckPerms = provider.getProvider();
        }
        plugin = this;
        taskManager = new TaskManager();
        teamsManager = new TeamsManager();
        godManager = new GodManager(taskManager, this);
        vanishManager = new VanishManager(this, taskManager, teamsManager, godManager);
        npcManager = new NPCManager(this);
        muteManager = new MuteManager(this, databaseManager);

        registerListeners();
        registerViaOnEnable(this);
    }

    @Override
    public void onDisable()
    {
    }

    public void connectToDatabase()
    {
        String host = getConfig().getString("database.host");
        int port = getConfig().getInt("database.port");
        String database = getConfig().getString("database.database");
        String username = getConfig().getString("database.username");
        String password = getConfig().getString("database.password");
        databaseManager = new DatabaseManager(host, port, database, username, password);

    }

    private void registerListeners()
    {
        getServer().getPluginManager()
                .registerEvents(new PlayerJoinListener(this, vanishManager, godManager, teamsManager, npcManager),
                        this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this, vanishManager, godManager), this);
        getServer().getPluginManager().registerEvents(new AsyncPlayerChatListener(this, muteManager), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractEventListener(), this);
        getServer().getPluginManager().registerEvents(new GodModeListener(godManager, vanishManager), this);
        new LuckPermsListener(this, teamsManager, vanishManager, luckPerms);
    }

    public void registerViaOnEnable(final SmCore plugin)
    {
        registerViaLifecycleEvents(plugin);
    }

    private void registerViaLifecycleEvents(final SmCore plugin)
    {
        final LifecycleEventManager<Plugin> lifecycleManager = plugin.getLifecycleManager();
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            NPCManager npcManager = plugin.getNpcManager();

            new GameModeCommand(plugin, commands);
            new NPCCommand(plugin, commands, npcManager);
            new VanishCommand(plugin, commands, vanishManager);
            new SpeedCommand(plugin, commands);
            new ClearCommand(plugin, commands);
            new GodCommand(plugin, commands, godManager);
            new FlyCommand(plugin, commands);
            new TeleportCommand(plugin, commands);
            new KickCommand(plugin, commands);
            new MuteCommand(plugin, commands, muteManager);
        });
    }

}
