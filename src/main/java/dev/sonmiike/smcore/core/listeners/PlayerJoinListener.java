package dev.sonmiike.smcore.core.listeners;

import dev.sonmiike.smcore.core.managers.TeamsManager;
import dev.sonmiike.smcore.core.managers.VanishManager;
import dev.sonmiike.smcore.core.util.MiniFormatter;
import dev.sonmiike.smcore.core.util.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final VanishManager vanishManager;
//    private final GodManager godManager;
    private final TeamsManager prefixManager;

    public PlayerJoinListener(VanishManager vanishManager, TeamsManager prefixManager) {
        this.vanishManager = vanishManager;
        this.prefixManager = prefixManager;
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        event.joinMessage(MiniFormatter.deserialize("<white>» ")
            .append(PlayerUtil.getPlayerNameWithRank(player))
            .append(MiniFormatter.deserialize("<gray> joined the server.")));

        updatePlayerVisibility(player);
        handleGodManager(player);
        updatePrefixForPlayer(player);


    }

    private void updatePlayerVisibility(Player player) {
        vanishManager.updateVisibilityForAllPlayers();
        vanishManager.handlePlayerJoin(player);
    }

    private void handleGodManager(Player player) {}

    private void updatePrefixForPlayer(Player player) {
        prefixManager.updateDisplayName(player, vanishManager.isVanished(player));
    }
}
