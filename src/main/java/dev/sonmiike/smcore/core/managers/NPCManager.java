package dev.sonmiike.smcore.core.managers;


import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPCManager {

    private final List<ServerPlayer> npcs;

    public NPCManager() {
        this.npcs = new ArrayList<>();
    }

    public void addNPC(ServerPlayer npc) {
        npcs.add(npc);
    }

    public void removeNPC(UUID uuid) {
        npcs.removeIf(npc -> npc.getUUID().equals(uuid));
    }

    public ServerPlayer getNPC(UUID uuid) {
        return npcs.stream().filter(npc -> npc.getUUID().equals(uuid)).findFirst().orElse(null);
    }

    public List<ServerPlayer> getAllNPCs() {
        return new ArrayList<>(npcs);
    }
}
