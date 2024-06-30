package dev.sonmiike.smcore.core.listeners;

import dev.sonmiike.smcore.SmCore;
import dev.sonmiike.smcore.core.managers.GodManager;
import dev.sonmiike.smcore.core.managers.NPCManager;
import dev.sonmiike.smcore.core.managers.TeamsManager;
import dev.sonmiike.smcore.core.managers.VanishManager;
import dev.sonmiike.smcore.core.model.NPC;
import dev.sonmiike.smcore.core.util.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import static dev.sonmiike.smcore.core.util.MiniFormatter.MM;


public class PlayerJoinListener implements Listener {

    private final SmCore plugin;
    private final VanishManager vanishManager;
    private final GodManager godManager;
    private final TeamsManager prefixManager;
    private final NPCManager npcManager;


    public PlayerJoinListener(SmCore plugin, VanishManager vanishManager, GodManager godManager, TeamsManager prefixManager, NPCManager npcManager) {
        this.plugin = plugin;
        this.vanishManager = vanishManager;
        this.godManager = godManager;
        this.prefixManager = prefixManager;
        this.npcManager = npcManager;
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        event.joinMessage(MM."<white>» "
            .append(MM."\{PlayerUtil.getPlayerNameWithRank(player)}")
            .append(MM."<gray> joined the server."));

        if (!plugin.getDatabaseManager().playerExists(player.getUniqueId())) {
            plugin.getDatabaseManager().addPlayer(player.getUniqueId(), player.getName(), player.getAddress().getAddress().getHostAddress());
        }

        handlePlayerVisibilityAndGodState(player);
        updatePrefixForPlayer(player);

        npcManager.getAllNPCs().values().forEach(NPC::sendPacketsToPlayers);

    }


    private void handlePlayerVisibilityAndGodState(Player player) {
        vanishManager.handlePlayerJoin(player);
        godManager.handlePlayerJoin(player);
        vanishManager.updateVisibilityForAllPlayers();
    }

    private void updatePrefixForPlayer(Player player) {
        prefixManager.updateDisplayName(player, vanishManager.isVanished(player));
    }
}



