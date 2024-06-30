package dev.sonmiike.smcore.core.listeners;

import dev.sonmiike.smcore.core.managers.TeamsManager;
import dev.sonmiike.smcore.core.managers.VanishManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.event.node.NodeClearEvent;
import net.luckperms.api.event.node.NodeRemoveEvent;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PrefixNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class LuckPermsListener  {

    private final JavaPlugin plugin;
    private final TeamsManager prefixSuffixManager;
    private final VanishManager vanishManager;

    public LuckPermsListener(JavaPlugin plugin, TeamsManager prefixSuffixManager, VanishManager vanishManager, LuckPerms luckPerms) {
        this.plugin = plugin;
        this.prefixSuffixManager = prefixSuffixManager;
        this.vanishManager = vanishManager;


        EventBus eventBus = luckPerms.getEventBus();

        eventBus.subscribe(this.plugin, UserDataRecalculateEvent.class, this::onGroupChange);
        eventBus.subscribe(this.plugin, NodeAddEvent.class, this::onNodeAdd);
        eventBus.subscribe(this.plugin, NodeRemoveEvent.class, this::onNodeRemove);
        eventBus.subscribe(this.plugin, NodeClearEvent.class, this::onClear);
    }

    void onGroupChange(UserDataRecalculateEvent event) {
        Player player = Bukkit.getPlayer(event.getUser().getUniqueId());
        if (player == null) return;
        prefixSuffixManager.updateDisplayName(player, vanishManager.isVanished(player));
    }

    private void onNodeRemove(NodeRemoveEvent e) {
        if (!e.isGroup()) {
            return;
        }
        Node node = e.getNode();
        if (node.getType() != NodeType.PREFIX) {
            return;
        }
        updatePlayers();
    }

    private void onClear(NodeClearEvent e) {
        if (!e.isGroup()) {
            return;
        }
        Set<Node> node = e.getNodes();
        if (node.stream().noneMatch(n -> n.getType() == NodeType.PREFIX)) {
            return;
        }
        updatePlayers();
    }

    private void onNodeAdd(NodeAddEvent e) {
        if (!e.isGroup()) {
            return;
        }
        Node node = e.getNode();
        if (node.getType() != NodeType.PREFIX) {
            return;
        }

        updatePlayers();
    }

    private void updatePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                prefixSuffixManager.updateDisplayName(player, vanishManager.isVanished(player));
            }, 4L);
        }
    }
}
