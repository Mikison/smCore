package dev.sonmiike.smcore.core.util;

import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.entity.Player;

public class PlayerUtil {

    public static Component getPlayerNameWithRank(Player player) {
        String prefix = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId())
            .getCachedData().getMetaData().getPrefix();
        return Component.text()
            .append(MiniFormatter.deserialize(prefix != null ? prefix + " " : ""))
            .append(MiniFormatter.deserialize("<reset>"))
            .append(player.displayName())
            .build();
    }
}
