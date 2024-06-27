package dev.sonmiike.smcore.core.util;

import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.entity.Player;

import static dev.sonmiike.smcore.core.util.MiniFormatter.MM;

public class PlayerUtil {

    public static Component getPlayerNameWithRank(Player player) {
        String prefix = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId())
            .getCachedData().getMetaData().getPrefix();
        String s = prefix != null ? STR."\{prefix} " : STR."";
        return Component.text()
            .append(MM."\{prefix != null ? STR."\{prefix} " : ""}")
            .append(MM."<reset>")
            .append(player.displayName())
            .build();
    }
}
