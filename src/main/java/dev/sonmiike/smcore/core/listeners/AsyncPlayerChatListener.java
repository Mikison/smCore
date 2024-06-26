package dev.sonmiike.smcore.core.listeners;

import dev.sonmiike.smcore.core.util.MiniFormatter;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AsyncPlayerChatListener implements Listener {


    @EventHandler
    void onPlayerChat(AsyncChatEvent event) {
        event.renderer(ChatRenderer.viewerUnaware((player, sourceDisplayName, message) -> {
            final User user = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());
            if (user == null) return Component.empty();

            final String prefix = user.getCachedData().getMetaData().getPrefix();
            return Component.empty()
                .append(MiniFormatter.deserialize("<gray>["))
                .append(prefix == null ? MiniFormatter.deserialize("<gray>PLAYER") : MiniFormatter.deserialize(prefix))
                .append(MiniFormatter.deserialize("<gray>] "))
                .append(sourceDisplayName)
                .append(MiniFormatter.deserialize("<white>: "))
                .append(passMessage(player, message));
        }));
    }

    private Component passMessage(Player player, Component component) {
        return hasColorPermission(player) ?
            MiniFormatter.deserialize(PlainTextComponentSerializer.plainText().serialize(component))
            : component;
    }


    private boolean isPlayerMuted(Player player) {
        return false;
    }

    private boolean isPlayerIgnored(Player player) {
        return false;
    }

    private boolean hasColorPermission(Player player) {
        return player.hasPermission("smcore.chat.color");
    }
}
