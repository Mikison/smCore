package dev.sonmiike.smcore.core.listeners;

import dev.sonmiike.smcore.core.managers.GodManager;
import dev.sonmiike.smcore.core.managers.VanishManager;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class GodModeListener implements Listener
{

    private final GodManager godManager;
    private final VanishManager vanishManager;

    public GodModeListener(GodManager godManager, VanishManager vanishManager)
    {
        this.godManager = godManager;
        this.vanishManager = vanishManager;
    }

    @EventHandler
    void onDamage(EntityDamageEvent event)
    {

        if (!(event.getEntity() instanceof Player player))
            return;
        if (godManager.isGod(player) || vanishManager.isVanished(player))
        {
            event.setDamage(0);
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
            player.setFoodLevel(20);

        }
    }

    @EventHandler
    void onFoodChange(FoodLevelChangeEvent event)
    {
        if (!(event.getEntity() instanceof Player player))
            return;
        if (godManager.isGod(player) || vanishManager.isVanished(player))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event)
    {
        Entity target = event.getTarget();
        if (target instanceof Player && (godManager.isGod((Player) target) || vanishManager.isVanished(
                (Player) target)))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
    {
        if (event.getEntity() instanceof Player && godManager.isGod((Player) event.getEntity()))
        {
            event.setCancelled(true);
        }
    }
}
