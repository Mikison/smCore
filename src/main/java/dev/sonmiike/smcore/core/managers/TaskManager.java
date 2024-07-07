package dev.sonmiike.smcore.core.managers;

import dev.sonmiike.smcore.core.tasks.ActionBarTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TaskManager
{

    private final Map<UUID, ActionBarTask> actionBarTasks = new HashMap<>();

    public void addTask(UUID playerUUID, ActionBarTask task)
    {
        actionBarTasks.put(playerUUID, task);
    }

    public void removeTask(UUID playerUUID)
    {
        final ActionBarTask task = actionBarTasks.remove(playerUUID);
        if (task != null)
        {
            task.cancel();
        }
    }

    public ActionBarTask getTask(UUID playerUUID)
    {
        return actionBarTasks.get(playerUUID);
    }
}
