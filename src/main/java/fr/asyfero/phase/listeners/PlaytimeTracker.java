package fr.asyfero.phase.listeners;

import fr.asyfero.phase.PointsManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class PlaytimeTracker implements Listener {

    PointsManager pointsManager;
    private final Map<Player, Long> loginTimes = new HashMap<>();

    public PlaytimeTracker(PointsManager pointsManager, JavaPlugin plugin) {
        this.pointsManager = pointsManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        loginTimes.put(player, System.currentTimeMillis() / 1000);
        loadPlaytime(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        long loginTime = loginTimes.getOrDefault(player, 0L);
        long playtime = (System.currentTimeMillis() / 1000) - loginTime;

        pointsManager.addData(player.getName(), playtime, ".playtime");

        loginTimes.remove(player);
    }

    public void loadPlaytime(Player player) {
        long playtime = pointsManager.getData(player.getName(), ".playtime");
    }
}
