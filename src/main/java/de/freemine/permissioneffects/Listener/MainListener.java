package de.freemine.permissioneffects.Listener;

import de.freemine.permissioneffects.Main;
import de.freemine.permissioneffects.util.Util;
import me.lucko.helper.Schedulers;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * @author LPkkjHD
 */
public class MainListener implements Listener {
    private final Main main;

    public MainListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void PlayerJoin(PlayerJoinEvent event) {
        Util.addEffects(event.getPlayer());
    }

    @EventHandler
    public void PlayerRespawnEvent(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        Util.addEffects(player);
    }

    @EventHandler
    public void PlayerEmptyBucket(PlayerItemConsumeEvent event) {
        final Player player = event.getPlayer();
        if (event.getItem().getType() == Material.MILK_BUCKET) {
            main.getLogger().info("Redoing effects for:" + player.getDisplayName() + " due to MilkBucket");
            Schedulers.sync().runLater(() -> {
                Util.addEffects(player);
                main.getLogger().info("added effects");
            }, 5L);
        }
    }

}
