package dev.sonmiike.smcore.core.listeners;

import dev.sonmiike.smcore.SmCore;
import dev.sonmiike.smcore.core.managers.GodManager;
import dev.sonmiike.smcore.core.managers.VanishManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener
{

    private final SmCore plugin;
    private final VanishManager vanishManager;
    private final GodManager godManager;

    public PlayerQuitListener(SmCore plugin, VanishManager vanishManager, GodManager godManager)
    {
        this.plugin = plugin;
        this.vanishManager = vanishManager;
        this.godManager = godManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        final Player player = event.getPlayer();

        plugin.getDatabaseManager()
                .updatePlayerLastJoin(player.getUniqueId(), player.getAddress().getAddress().getHostAddress());

        handleVanishAndGod(player);
        event.quitMessage(Component.empty());
    }

    private void handleVanishAndGod(Player player)
    {
        vanishManager.handlePlayerQuit(player);
        godManager.handlePlayerQuit(player);
        if (!player.getScoreboard().getTeams().isEmpty())
        {
            player.getScoreboard().getTeam(player.getName()).unregister();

        }
    }
}
