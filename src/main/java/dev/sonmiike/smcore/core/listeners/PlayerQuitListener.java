package dev.sonmiike.smcore.core.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

//    private final VanishManager vanishManager;
//
//    public PlayerQuitListener(VanishManager vanishManager) {
//        this.vanishManager = vanishManager;
//    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
//        vanishManager.handlePlayerQuit(player); // TODO Extract it to private methods
//        if (!player.getScoreboard().getTeams().isEmpty()) {    // TODO Inject ScoreboardManager and create handlePlayerQuit method there to handle this
//            player.getScoreboard().getTeam(player.getName()).unregister();
//
//        }

        event.quitMessage(Component.empty());
    }
}
