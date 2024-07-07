package dev.sonmiike.smcore.core.managers;

import dev.sonmiike.smcore.SmCore;
import dev.sonmiike.smcore.core.model.MuteInfo;
import dev.sonmiike.smcore.core.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MuteManager
{
    private final SmCore plugin;
    private final DatabaseManager databaseManager;
    private final Map<UUID, MuteInfo> mutedPlayers;

    public MuteManager(SmCore plugin, DatabaseManager databaseManager)
    {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        this.mutedPlayers = new HashMap<>();
        loadMutedPlayers();
        startMuteCheckTask();
    }

    private void loadMutedPlayers()
    {
        List<MuteInfo> muteInfos = databaseManager.loadAllMutedPlayers();
        for (MuteInfo info : muteInfos)
        {
            mutedPlayers.put(info.uuid(), info);
        }
    }

    private void startMuteCheckTask()
    {

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::checkMutes, 20L, 20L * 60);
    }

    private void checkMutes()
    {
        for (Map.Entry<UUID, MuteInfo> entry : mutedPlayers.entrySet())
        {
            UUID uuid = entry.getKey();
            MuteInfo muteInfo = entry.getValue();
            if (muteInfo.expiresAt() == null)
                continue;
            if (muteInfo.expiresAt().isBefore(LocalDateTime.now()))
            {
                unmutePlayer(uuid);
            }
        }
    }

    public void mutePlayer(UUID uuid, String reason, String mutedBy, LocalDateTime muteDate, LocalDateTime expiresAt)
    {
        MuteInfo muteInfo = new MuteInfo(uuid, reason, mutedBy, muteDate, expiresAt);
        mutedPlayers.put(uuid, muteInfo);
        databaseManager.mutePlayer(uuid, reason, mutedBy, muteDate, expiresAt);
    }

    public void unmutePlayer(UUID uuid)
    {
        mutedPlayers.remove(uuid);
        databaseManager.updateMuteStatus(uuid, false);
    }

    public boolean isPlayerMuted(UUID uuid)
    {
        MuteInfo muteInfo = mutedPlayers.get(uuid);
        if (muteInfo == null)
            return false;
        if (muteInfo.expiresAt() == null)
            return true;
        return muteInfo.expiresAt().isAfter(LocalDateTime.now());
    }

    public boolean mapContainsPlayer(UUID uuid)
    {
        return mutedPlayers.containsKey(uuid);
    }

    public String getMuteReason(UUID uuid)
    {
        MuteInfo muteInfo = mutedPlayers.get(uuid);
        return muteInfo != null ? muteInfo.reason() : null;
    }

    public String getDuration(UUID uuid)
    {
        MuteInfo muteInfo = mutedPlayers.get(uuid);
        if (muteInfo == null)
            return null;
        return muteInfo.expiresAt() == null ?
                "<bold><dark_red>PERMANENT</bold>" :
                TimeUtils.formatDuration(Duration.between(LocalDateTime.now(), muteInfo.expiresAt()));
    }

    public CommandSender getMutedBy(UUID uuid)
    {
        MuteInfo muteInfo = mutedPlayers.get(uuid);
        return muteInfo.mutedBy().equals("CONSOLE") ? Bukkit.getConsoleSender() : Bukkit.getPlayer(muteInfo.mutedBy());
    }

}

