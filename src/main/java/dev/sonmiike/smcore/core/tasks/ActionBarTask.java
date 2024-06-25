package dev.sonmiike.smcore.core.tasks;

import dev.sonmiike.smcore.core.util.MiniFormatter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionBarTask extends BukkitRunnable {


    private final Player player;
    private boolean isVanished;
    private boolean isGodMode;

    public ActionBarTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (player.isOnline()) {
            updateActionBar();
        } else {
            cancel();
        }
    }

    public void setVanished(boolean isVanished) {
        this.isVanished = isVanished;
        updateActionBar();
    }

    public void setGodMode(boolean isGodMode) {
        this.isGodMode = isGodMode;
        updateActionBar();
    }

    private void updateActionBar() {
        StringBuilder sb = new StringBuilder();

        if (isVanished) sb.append("<blue>VANISHED");
        if (isGodMode) {
            if (!sb.isEmpty()) {
                sb.append(" <white>| ");
            }
            sb.append("<gold>GOD");
        }

        if (sb.isEmpty()) {
            cancel();
        } else {
            player.sendActionBar(
                MiniFormatter.deserialize("<white>» " + sb + " <white>«"));
        }
    }

    public boolean isVanished() {
        return isVanished;
    }

    public boolean isGodMode() {
        return isGodMode;
    }
}
