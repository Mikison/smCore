package dev.sonmiike.smcore.core.listeners;

import dev.sonmiike.smcore.SmCore;
import dev.sonmiike.smcore.core.managers.MuteManager;
import dev.sonmiike.smcore.core.util.MiniFormatter;
import dev.sonmiike.smcore.core.util.PlayerUtil;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static dev.sonmiike.smcore.core.util.MiniFormatter.MM;

public class AsyncPlayerChatListener implements Listener {

    private final SmCore plugin;
    private final MuteManager muteManager;

    public AsyncPlayerChatListener(SmCore plugin, MuteManager muteManager) {
        this.plugin = plugin;
        this.muteManager = muteManager;
    }

    @EventHandler
    void onPlayerChat(AsyncChatEvent event) {
        Player sender = event.getPlayer();
        if (isPlayerMuted(sender)) {
            event.setCancelled(true);
            sender.sendMessage(MM."<bold><dark_gray>[<red>!<dark_gray>]</bold> <red>You are muted! <dark_gray>| <red>Reason: <gray>\{muteManager.getMuteReason(sender.getUniqueId())} <dark_gray>| <red>Expires in: <gray>\{muteManager.getDuration(sender.getUniqueId())} <dark_gray>| <red>Muted by: <gray>\{PlayerUtil.getPlayerNameWithRank(muteManager.getMutedBy(sender.getUniqueId()))}");
            return;
        }
        if (!isPlayerMuted(sender) && muteManager.mapContainsPlayer(sender.getUniqueId())) {
            muteManager.unmutePlayer(sender.getUniqueId());
        }


        event.renderer(ChatRenderer.viewerUnaware((player, sourceDisplayName, message) -> {
            final User user = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());
            if (user == null) return Component.empty();
            final String prefix = user.getCachedData().getMetaData().getPrefix();
            return Component.empty()
                .append(MM."<gray>[")
                .append(prefix == null ? MM."<gray>PLAYER" : MM."\{prefix}")
                .append(MM."<gray>] ")
                .append(sourceDisplayName)
                .append(MM."<white>: ")
                .append(passMessage(player, message));
        }));
    }

    private Component passMessage(Player player, Component component) {
        return hasColorPermission(player) ?
            MM."\{PlainTextComponentSerializer.plainText().serialize(component)}"
            : component;
    }


    private boolean isPlayerMuted(Player player) {
        return muteManager.isPlayerMuted(player.getUniqueId());
    }

    private boolean isPlayerIgnored(Player player) {
        return false;
    }

    private boolean hasColorPermission(Player player) {
        return player.hasPermission("smcore.chat.color");
    }
}
