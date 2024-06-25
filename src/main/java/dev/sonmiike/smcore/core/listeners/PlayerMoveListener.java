package dev.sonmiike.smcore.core.listeners;

import dev.sonmiike.smcore.SmCore;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.EnumSet;

public class PlayerMoveListener implements Listener {


    private SmCore plugin;

    public PlayerMoveListener(SmCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    void onPlayerMove(PlayerMoveEvent event) {
        plugin.getNpcManager().getAllNPCs().stream()
            .forEach(npc -> {
                Location location = npc.getBukkitEntity().getLocation();
                location.setDirection(event.getPlayer().getLocation().subtract(location).toVector());
                float yaw = location.getYaw();
                float pitch = location.getPitch();

                ServerGamePacketListenerImpl packetListener = ((CraftPlayer) event.getPlayer()).getHandle().connection;
                packetListener.send(new ClientboundRotateHeadPacket(npc, (byte) ((yaw%360)*256/360) ));
            });
    }
}
