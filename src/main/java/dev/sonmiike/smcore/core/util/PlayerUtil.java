package dev.sonmiike.smcore.core.util;

import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static dev.sonmiike.smcore.core.util.MiniFormatter.MM;

public class PlayerUtil {

    public static String getPlayerNameWithRank(Player player) {
        String prefix = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId())
            .getCachedData().getMetaData().getPrefix();
        String s = prefix != null ? STR."\{prefix} " : "";
        return STR."<reset>\{s}<reset>\{player.getName()}";
    }

    public static boolean playerHasPermission(CommandSender sender, String permission) {
        if (sender.hasPermission(permission)) return true;
        sender.sendMessage(MM."<bold><dark_gray>[<red>!<dark_gray>]</bold> <red>You do not have permission to use this command! <gray>(\{permission})");
        return false;
    }
}
