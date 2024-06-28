package dev.sonmiike.smcore.core.listeners;


import dev.sonmiike.smcore.core.regions.Region;
import dev.sonmiike.smcore.core.regions.RegionUtil;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractEventListener implements Listener {

    private final Region region = new Region();

    @EventHandler
    void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().isRightClick() && event.hasBlock()) {
            if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.WOODEN_AXE) {
                region.setCorner1(event.getClickedBlock().getLocation());


            } else if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.GOLDEN_AXE) {
                region.setCorner2(event.getClickedBlock().getLocation());

            }
        }
        if (region.isSet()) {
            RegionUtil.drawSelector(region, region.getWorld(), "region", NamedTextColor.GREEN);
        }

        if (event.getAction().isLeftClick()) {
            if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.STICK) {
                RegionUtil.killAllSelectors();
                region.setCorner2(null);
                region.setCorner1(null);
                RegionUtil.removeTempTeams();
            }
        }
    }
}
