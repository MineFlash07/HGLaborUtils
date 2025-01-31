package de.hglabor.utils.noriskutils.staffmode;

import de.hglabor.utils.localization.Localization;
import de.hglabor.utils.noriskutils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerHider implements Listener {
    private final JavaPlugin plugin;
    private final StaffPlayerSupplier supplier;

    public PlayerHider(StaffPlayerSupplier supplier, JavaPlugin plugin) {
        this.supplier = supplier;
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        //haha joint
        Player joined = event.getPlayer();
        StaffPlayer staffPlayer = supplier.getStaffPlayer(joined);
        if (staffPlayer.canSeeStaffModePlayers()) {
            return;
        }
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            StaffPlayer otherStaffPlayer = supplier.getStaffPlayer(otherPlayer);
            if (otherStaffPlayer.isStaffMode() && !otherStaffPlayer.isVisible()) {
                joined.hidePlayer(plugin, otherPlayer);
            }
        }
    }

    public void hide(Player playerToHide) {
        supplier.getStaffPlayer(playerToHide).setVisible(false);
        playerToHide.sendActionBar(Localization.INSTANCE.getMessage("staffmode.hidden", ChatUtils.locale(playerToHide)));
        for (Player player : Bukkit.getOnlinePlayers()) {
            StaffPlayer staffPlayer = supplier.getStaffPlayer(player);
            if (player.hasPermission("hglabor.staffmode")) {
                if (staffPlayer.canSeeStaffModePlayers()) continue;
                if (staffPlayer.isStaffMode()) continue;
            }
            player.hidePlayer(plugin, playerToHide);
        }
    }

    public void show(Player playerToShow) {
        supplier.getStaffPlayer(playerToShow).setVisible(true);
        playerToShow.sendActionBar(Localization.INSTANCE.getMessage("staffmode.visible", ChatUtils.locale(playerToShow)));
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showPlayer(plugin, playerToShow);
        }
    }

    public void hideEveryoneInStaffMode(Player player) {
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            StaffPlayer staffPlayer = supplier.getStaffPlayer(otherPlayer);
            if (staffPlayer.isStaffMode()) {
                player.hidePlayer(plugin, otherPlayer);
            }
        }
    }

    public void showEveryoneInStaffMode(Player player) {
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            StaffPlayer staffPlayer = supplier.getStaffPlayer(otherPlayer);
            if (staffPlayer.isStaffMode()) {
                player.showPlayer(plugin, otherPlayer);
            }
        }
    }

    public StaffPlayerSupplier getSupplier() {
        return supplier;
    }

    public void sendHideInformation() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            StaffPlayer staffPlayer = supplier.getStaffPlayer(player);
            if (!staffPlayer.isStaffMode()) continue;
            if (staffPlayer.isVisible()) {
                player.sendActionBar(Localization.INSTANCE.getMessage("staffmode.visible", ChatUtils.locale(player)));
            } else {
                player.sendActionBar(Localization.INSTANCE.getMessage("staffmode.hidden", ChatUtils.locale(player)));
            }
        }
    }
}
