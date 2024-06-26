package dev.sonmiike.smcore.core.listeners;

import dev.sonmiike.smcore.SmCore;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {


    private SmCore plugin;

    public PlayerMoveListener(SmCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    void onPlayerMove(PlayerMoveEvent event) {
        plugin.getNpcManager().getAllNPCs().stream()
            .forEach(npc -> {
                Location npcLocation = npc.getBukkitEntity().getLocation();
                Location playerLocation = event.getPlayer().getLocation();
                if (npcLocation.distance(playerLocation) > 20) return;
                npcLocation.setDirection(playerLocation.subtract(npcLocation).toVector());
                float yaw = npcLocation.getYaw();
                float pitch = npcLocation.getPitch();

                ServerGamePacketListenerImpl packetListener = ((CraftPlayer) event.getPlayer()).getHandle().connection;
                packetListener.send(new ClientboundRotateHeadPacket(npc, (byte) ((yaw % 360) * 256 / 360)));
                packetListener.send(new ClientboundMoveEntityPacket.Rot(npc.getId(), (byte) ((yaw % 360) * 256 / 360), (byte) ((pitch % 360) * 256 / 360), true));

            });
    }
}
