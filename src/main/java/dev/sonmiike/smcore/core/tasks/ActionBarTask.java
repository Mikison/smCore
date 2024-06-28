package dev.sonmiike.smcore.core.tasks;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static dev.sonmiike.smcore.core.util.MiniFormatter.MM;

public class ActionBarTask extends BukkitRunnable {

    private final Player player;
    @Getter
    private boolean isVanished;
    @Getter
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
            sb.append("<blue>GOD");
        }

        if (sb.isEmpty()) {
            cancel();
        } else {
            player.sendActionBar(MM."<white>»  \{sb}  <white>«");
        }
    }

}
