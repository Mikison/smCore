package dev.sonmiike.smcore.core.listeners;

import dev.sonmiike.smcore.core.managers.NPCManager;
import dev.sonmiike.smcore.core.managers.TeamsManager;
import dev.sonmiike.smcore.core.managers.VanishManager;
import dev.sonmiike.smcore.core.model.NPC;
import dev.sonmiike.smcore.core.util.MiniFormatter;
import dev.sonmiike.smcore.core.util.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static dev.sonmiike.smcore.core.util.MiniFormatter.MM;

public class PlayerJoinListener implements Listener {

    private final VanishManager vanishManager;
//    private final GodManager godManager;
    private final TeamsManager prefixManager;
    private final NPCManager npcManager;

    public PlayerJoinListener(VanishManager vanishManager, TeamsManager prefixManager, NPCManager npcManager) {
        this.vanishManager = vanishManager;
        this.prefixManager = prefixManager;
        this.npcManager = npcManager;
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        event.joinMessage(MM."<white>» "
            .append(MM."\{PlayerUtil.getPlayerNameWithRank(player)}")
            .append(MM."<gray> joined the server."));

        updatePlayerVisibility(player);
        handleGodManager(player);
        updatePrefixForPlayer(player);

        npcManager.getAllNPCs().values().forEach(NPC::sendPacketsToPlayers);

    }

    private void updatePlayerVisibility(Player player) {
        if (vanishManager.isVanished(player)) {
            player.sendMessage(MM."<bold><dark_gray>[<blue>!<dark_gray>]</bold> <gray>Joined <blue>VANISHED<gray> to the server");
        }
        vanishManager.updateVisibilityForAllPlayers();
        vanishManager.handlePlayerJoin(player);
    }

    private void handleGodManager(Player player) {}

    private void updatePrefixForPlayer(Player player) {
        prefixManager.updateDisplayName(player, vanishManager.isVanished(player));
    }
}
