package dev.sonmiike.smcore.core.commands;

import com.mojang.brigadier.Command;
import dev.sonmiike.smcore.SmCore;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;

public final class Registration {

        private Registration() {}

        public static void registerViaOnEnable(final SmCore plugin) {
            registerViaLifecycleEvents(plugin);
        }

        private static void registerViaLifecycleEvents(final SmCore plugin) {
            final LifecycleEventManager<Plugin> lifecycleManager = plugin.getLifecycleManager();
            lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
                final Commands commands = event.registrar();

                GameModeCommand.register(plugin, commands);
                NPCCommand.register(plugin, commands);
            });
        }
}
