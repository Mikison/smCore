package dev.sonmiike.smcore.core.managers;

import dev.sonmiike.smcore.SmCore;
import dev.sonmiike.smcore.core.model.NPC;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class NPCManager {


    private final SmCore plugin;

    private final HashMap<String, NPC> npcs = new HashMap<>();

    public NPCManager(SmCore plugin) {
        this.plugin = plugin;
    }


    public void createNPC(MinecraftServer server, ServerLevel level, String texture, String signature, Location location) {
        NPC npc = new NPC(server, level, texture, signature, location);
        final String npcName = npc.getNpcPlayer().getGameProfile().getName();
        npcs.put(npcName, npc);
        npc.sendPacketsToPlayers();
        scheduleTextDisplayRemoval(npc);
    }

    public NPC getNPC(String npcName) {
        return npcs.get(npcName);
    }

    public HashMap<String, NPC> getAllNPCs() {
        return new HashMap<>(npcs);
    }

    private void scheduleTextDisplayRemoval(NPC npc) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!npc.getNpcPlayer().isAlive()) {
                    npc.removeTextDisplay();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20 * 60); // Runs every minute
    }
}
