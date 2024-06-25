package dev.sonmiike.smcore.core.managers;

import dev.sonmiike.smcore.core.util.MiniFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.minecraft.world.entity.monster.warden.Warden;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TeamsManager {

    public static final String VANISHED_SUFFIX = " <gray>[<blue>VANISHED<gray>]";

    public void updateDisplayName(Player player, boolean isVanished) {
        final Scoreboard scoreboard = player.getScoreboard();
        Team team = scoreboard.getTeam(player.getName());
        if (team == null) {
            team = scoreboard.registerNewTeam(player.getName());
        }

        final Component suffix = isVanished ? MiniFormatter.deserialize(VANISHED_SUFFIX) : Component.empty();
        final Component prefix = getPrefixBasedOnPermission(player);
        team.prefix(prefix);

        team.suffix(suffix);
        team.addEntry(player.getName());

        player.setGlowing(true);
        player.setScoreboard(scoreboard);
    }

    private Component getPrefixBasedOnPermission(Player player) {
        final User user = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());
        final String prefix = user.getCachedData().getMetaData().getPrefix();
        if (prefix == null) return Component.empty();
        return MiniFormatter.deserialize(prefix + " <white>» ");
    }
}
