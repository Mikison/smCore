package dev.sonmiike.smcore.core.listeners;

import dev.sonmiike.smcore.core.managers.VanishManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final VanishManager vanishManager;

    public PlayerQuitListener(VanishManager vanishManager) {
        this.vanishManager = vanishManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        event.quitMessage(Component.empty());
    }

    private void handleVanishManager(Player player) {
        vanishManager.handlePlayerQuit(player);
        if (!player.getScoreboard().getTeams().isEmpty()) {
            player.getScoreboard().getTeam(player.getName()).unregister();

        }
    }
}
