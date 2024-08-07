package dev.sonmiike.smcore.core.managers;

import dev.sonmiike.smcore.core.tasks.ActionBarTask;
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

import static dev.sonmiike.smcore.core.util.MiniFormatter.MM;

public class VanishManager
{

    private final JavaPlugin instance;
    private final TaskManager taskManager;
    private final TeamsManager teamsManager;
    private final GodManager godManager;

    private final Set<UUID> vanishedPlayers = new HashSet<>();

    public VanishManager(JavaPlugin instance, TaskManager taskManager, TeamsManager teamsManager, GodManager godManager)
    {
        this.instance = instance;
        this.taskManager = taskManager;
        this.teamsManager = teamsManager;
        this.godManager = godManager;
    }

    public boolean isVanished(Player player)
    {
        return vanishedPlayers.contains(player.getUniqueId());
    }

    public void toggleVanish(Player player)
    {
        setVanished(player, !isVanished(player));
    }

    private void setVanished(Player player, boolean vanished)
    {
        if (vanished)
        {
            vanishedPlayers.add(player.getUniqueId());
            applyVanishState(player);
            player.sendMessage(MM."<bold><dark_gray>[<blue>!<dark_gray>]</bold> <gray>You are now <blue>VANISHED");
        }
        else
        {
            vanishedPlayers.remove(player.getUniqueId());
            player.sendMessage(MM."<bold><dark_gray>[<blue>!<dark_gray>]</bold> <gray>You are now <blue>VISIBLE");
        }
        handleActionBarTask(player);
        updatePlayerVisibility(player);
        teamsManager.updateDisplayName(player, isVanished(player));
    }

    private void handleActionBarTask(Player player)
    {
        ActionBarTask task = taskManager.getTask(player.getUniqueId());
        boolean isVanished = isVanished(player);

        if (isVanished)
        {
            if (task == null)
            {
                task = new ActionBarTask(player);
                taskManager.addTask(player.getUniqueId(), task);
                task.runTaskTimer(instance, 0L, 40L);
            }
            task.setVanished(true);
        }
        else if (task != null && !task.isGodMode())
        {
            taskManager.removeTask(player.getUniqueId());
            task.cancel();
        }
        else
        {
            task.setVanished(false);
        }
    }

    private void applyVanishState(Player player)
    {
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        player.setFoodLevel(20);
        setNearbyEntitiesTargetNull(player);
    }

    private void updatePlayerVisibility(Player player)
    {
        for (Player other : Bukkit.getOnlinePlayers())
        {
            if (player.hasPermission("smcore.vanish"))
            {
                player.showPlayer(instance, other);
            }
            else if (isVanished(other))
            {
                player.hidePlayer(instance, other);
            }
            else
            {
                player.showPlayer(instance, other);
            }
        }
    }

    public void updateVisibilityForAllPlayers()
    {
        for (Player player : Bukkit.getOnlinePlayers())
        {
            updatePlayerVisibility(player);
        }
    }

    public void handlePlayerJoin(Player player)
    {
        if (!player.hasPermission("smcore.vanish") && isVanished(player))
        {
            setVanished(player, false);
        }

        if (isVanished(player))
        {
            handleActionBarTask(player);
            updatePlayerVisibility(player);
        }
        if (player.hasPermission("smcore.vanish") && !isVanished(player))
        {
            setVanished(player, true);
            updatePlayerVisibility(player);
        }
    }

    public void handlePlayerQuit(Player player)
    {
        if (taskManager.getTask(player.getUniqueId()) != null)
            taskManager.removeTask(player.getUniqueId());
    }

    private void setNearbyEntitiesTargetNull(Player player)
    {
        final List<Entity> nearbyEntities = player.getNearbyEntities(30, 30, 30);
        for (Entity entities : nearbyEntities)
        {
            if (entities instanceof Creature creature)
            {
                creature.setTarget(null);
            }
        }
    }
}
