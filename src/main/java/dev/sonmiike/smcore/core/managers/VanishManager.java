package dev.sonmiike.smcore.core.managers;

import dev.sonmiike.smcore.core.tasks.ActionBarTask;
import dev.sonmiike.smcore.core.util.MiniFormatter;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class VanishManager {

    private final JavaPlugin instance;
    private final TaskManager taskManager;

    private final Set<UUID> vanishedPlayers = new HashSet<>();

    public VanishManager(JavaPlugin instance, TaskManager taskManager) {
        this.instance = instance;
        this.taskManager = taskManager;
    }

    public boolean isVanished(Player player) {
        return vanishedPlayers.contains(player.getUniqueId());
    }

    public void toggleVanish(Player player) {
        setVanished(player, !isVanished(player));
    }

    private void setVanished(Player player, boolean vanished) {
        if (vanished) {
            vanishedPlayers.add(player.getUniqueId());
            handleActionBarTask(player, true);
            applyVanishState(player);
        } else {
            vanishedPlayers.remove(player.getUniqueId());
            handleActionBarTask(player, false);
        }
        updatePlayerVisibility(player);
        // TODO ScoreboardManager here <-
    }

    private void handleActionBarTask(Player player, boolean start) {
        ActionBarTask task = taskManager.getTask(player.getUniqueId());
        if (start) {
            if (task == null) {
                task = new ActionBarTask(player);
                taskManager.addTask(player.getUniqueId(), task);
                task.runTaskTimer(instance, 0L, 40L);
            }
            task.setVanished(true);
            return;
        }
        if (task != null) {
            task.setVanished(false);
            if (!task.isVanished() && !task.isGodMode()) {
                taskManager.removeTask(player.getUniqueId());
                task.cancel();
            }
        }

    }

    private void applyVanishState(Player player) {
//        player.sendMessage(mm.deserialize("<white>» <gray>You are now <blue>VANISHED")); // TODO Think about where to send messages manager or command
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        player.setFoodLevel(20);
        setNearbyEntitiesTargetNull(player);
    }

    private void updatePlayerVisibility(Player player) {
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("smcore.vanish")) {
                player.showPlayer(instance, other);
            } else if (isVanished(other)) {
                player.hidePlayer(instance, other);
            } else {
                player.showPlayer(instance, other);
            }
        }
    }

    public void updateVisibilityForAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerVisibility(player);
        }
    }

    public void handlePlayerJoin(Player player) {
        if (!player.hasPermission("smcore.vanish") && isVanished(player)) {
            setVanished(player, false);
        }
        if (isVanished(player)) {
            handleActionBarTask(player, true);
            updatePlayerVisibility(player);
        } else if (player.hasPermission("smcore.vanish")) {
//            player.sendMessage(MiniFormatter.deserialize("<white>» <gray>Joined the game in <blue>VANISHED<gray> mode")); // TODO think about it
            setVanished(player, true);
        }
    }

    public void handlePlayerQuit(Player player) {
        taskManager.removeTask(player.getUniqueId());
    }

    private void setNearbyEntitiesTargetNull(Player player) {
        final List<Entity> nearbyEntities = player.getNearbyEntities(30  , 30, 30);
        for (Entity entities : nearbyEntities) {
            if (entities instanceof Creature creature) {
                creature.setTarget(null);
            }
        }
    }


}
