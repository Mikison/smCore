package dev.sonmiike.smcore.core.managers;

import dev.sonmiike.smcore.SmCore;
import dev.sonmiike.smcore.core.tasks.ActionBarTask;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class GodManager {

    private final TaskManager taskManager;
    private final SmCore plugin;

    public GodManager(TaskManager taskManager, SmCore plugin) {
        this.taskManager = taskManager;
        this.plugin = plugin;
    }

    private final Set<UUID> godPlayers = new HashSet<>();

    public boolean isGod(Player player) {
        return godPlayers.contains(player.getUniqueId());
    }

    public void toggleGod(Player player) {
        setGod(player, !isGod(player));
    }

    private void setGod(Player player, boolean god) {
        if (god) {
            godPlayers.add(player.getUniqueId());
            handleActionBarTask(player);
            applyGodState(player);
        } else {
            godPlayers.remove(player.getUniqueId());
            handleActionBarTask(player);
        }
    }

    private void applyGodState(Player player) {
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        player.setFoodLevel(20);
        setNearbyEntitiesTargetNull(player);
    }

    private void handleActionBarTask(Player player) {
        ActionBarTask task = taskManager.getTask(player.getUniqueId());
        boolean isGod = isGod(player);
        boolean isVanished = task != null && task.isVanished();

        if (isGod || isVanished) {
            if (task == null) {
                task = new ActionBarTask(player);
                taskManager.addTask(player.getUniqueId(), task);
                task.runTaskTimer(plugin, 0L, 40L);
            }
            task.setGodMode(isGod);
            task.setVanished(isVanished);
        } else if (task != null) {
            taskManager.removeTask(player.getUniqueId());
            task.cancel();
        }
    }

    private void setNearbyEntitiesTargetNull(Player player) {
        List<Entity> nearbyEntities = player.getNearbyEntities(30, 30, 30);
        for (Entity entities : nearbyEntities) {
            if (entities instanceof Creature creature) {
                creature.setTarget(null);
            }
        }
    }
}
