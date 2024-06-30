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

import static dev.sonmiike.smcore.core.util.MiniFormatter.MM;

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
            applyGodState(player);
            player.sendMessage(MM."<bold><dark_gray>[<blue>!<dark_gray>]</bold> <gray>God mode <blue>ENABLED");
        } else {
            godPlayers.remove(player.getUniqueId());
            player.sendMessage(MM."<bold><dark_gray>[<blue>!<dark_gray>]</bold> <gray>God mode <blue>DISABLED");
        }
        handleActionBarTask(player);
    }

    private void applyGodState(Player player) {
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        player.setFoodLevel(20);
        setNearbyEntitiesTargetNull(player);
    }

    private void handleActionBarTask(Player player) {
        ActionBarTask task = taskManager.getTask(player.getUniqueId());
        boolean isGod = isGod(player);

        if (isGod) {
            if (task == null) {
                task = new ActionBarTask(player);
                taskManager.addTask(player.getUniqueId(), task);
                task.runTaskTimer(plugin, 0L, 40L);
            }
            task.setGodMode(isGod);
        } else if (task != null && !task.isVanished()) {
            taskManager.removeTask(player.getUniqueId());
            task.cancel();
        } else {
            task.setGodMode(isGod);
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

    public void handlePlayerJoin(Player player) {
        if (!player.hasPermission("smcore.god") && isGod(player)) {
            setGod(player, false);
        }

        if (isGod(player)) {
            handleActionBarTask(player);
        }
    }

//    public void handlePlayerJoin(Player player) {
//        if (!player.hasPermission("smcore.god") && isGod(player)) {
//            setGod(player, false);
//        }
//
//        if (isGod(player)) {
//            handleActionBarTask(player);
//        } else if (player.hasPermission("smcore.vanish")) {
//            handleActionBarTask(player);
//            setGod(player, true);
//        }
//    }

    public void handlePlayerQuit(Player player) {
        if (taskManager.getTask(player.getUniqueId()) != null)
            taskManager.removeTask(player.getUniqueId());
    }
}
