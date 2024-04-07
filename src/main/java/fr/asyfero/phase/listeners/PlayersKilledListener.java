package fr.asyfero.phase.listeners;

import fr.asyfero.phase.PointsManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class PlayersKilledListener implements Listener {

    private final PointsManager pointsManager;
    private final FileConfiguration dataConfig;
    private final File dataFile;
    private final Map<Player, Integer> playersKilled = new HashMap<>();
    private final Map<Player, Instant> lastKillTimes = new HashMap<>();
    private final Map<Player, Duration> remainingCooldowns = new HashMap<>();
    private static final Duration COOLDOWN_DURATION = Duration.ofHours(1);
    private BukkitTask cooldownTimer;
    private static JavaPlugin plugin;

    public PlayersKilledListener(PointsManager pointsManager, JavaPlugin plugin) {
        this.plugin = plugin;
        this.pointsManager = pointsManager;

        dataFile = new File(plugin.getDataFolder(), "player_cooldown.yml");
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        loadPlayersKilled();
    }

    @EventHandler
    public void onPlayerKill(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getEntity().getKiller() != null) {
                Player playerKiller = event.getEntity().getKiller();
                Instant lastKillTime = lastKillTimes.get(playerKiller);
                Instant currentTime = Instant.now();

                if (lastKillTime != null && Duration.between(lastKillTime, currentTime).compareTo(COOLDOWN_DURATION) < 0) {
                    return;
                }

                String playerName = playerKiller.getName();

                long currentCount = pointsManager.getData(playerName, ".playerskilled");
                pointsManager.addData(playerName, 1, ".playerskilled");

                long playersGetKilled = playersKilled.getOrDefault(playerKiller, 0) + 1;
                playersKilled.put(playerKiller, (int) playersGetKilled);

                if (playersGetKilled == 1) {
                    pointsManager.addData(playerName, 1, ".points");
                    Bukkit.broadcast(Component.text(ChatColor.GOLD + playerName + " a obtenu " + ChatColor.RED + 1 + ChatColor.GOLD + " point avec le métier d'Assassin !"));
                    playerKiller.playSound(playerKiller.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);

                    playersKilled.put(playerKiller, 0);
                    lastKillTimes.put(playerKiller, Instant.now());
                    remainingCooldowns.put(playerKiller, COOLDOWN_DURATION);
                }

                pointsManager.saveFile();
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        loadPlayersKilled(player);
        loadRemainingCooldowns();
        if (cooldownTimer == null) {
            startCooldownTimer();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        pointsManager.saveFile();
        playersKilled.remove(player);
        long remainingCooldown = remainingCooldowns.getOrDefault(player, Duration.ZERO).toSeconds();
        pointsManager.addData(player.getName(), remainingCooldown, ".playerskilledcooldown");
        remainingCooldowns.remove(player);
    }

    private void loadPlayersKilled() {
        for (String playerName : dataConfig.getKeys(false)) {
            long playersKilledCount = pointsManager.getData(playerName, ".playerskilled");
            Player player = Bukkit.getPlayerExact(playerName);
            if (player != null) {
                playersKilled.put(player, (int) playersKilledCount);
            }
        }
    }

    private void loadPlayersKilled(Player player) {
        long playersKilledCount = pointsManager.getData(player.getName(), ".playerskilled");
        playersKilled.put(player, (int) playersKilledCount);
    }

    public void loadRemainingCooldowns() {
        for (String playerName : dataConfig.getKeys(false)) {
            long remainingCooldownSeconds = dataConfig.getLong(playerName + ".playerskilledcooldown", 0);
            if (remainingCooldownSeconds > 0) {
                remainingCooldowns.put(Bukkit.getPlayerExact(playerName), Duration.ofSeconds(remainingCooldownSeconds));
            }
        }
    }

    private void saveRemainingCooldowns() {
        for (Map.Entry<Player, Duration> entry : remainingCooldowns.entrySet()) {
            Player player = entry.getKey();
            Duration remainingCooldown = entry.getValue();
            String playerName = player.getName();
            dataConfig.set(playerName + ".playerskilledcooldown", remainingCooldown.getSeconds());
        }

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startCooldownTimer() {
        cooldownTimer = new BukkitRunnable() {
            @Override
            public void run() {
                decrementCooldowns();
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    private void decrementCooldowns() {
        for (Map.Entry<Player, Duration> entry : remainingCooldowns.entrySet()) {
            Player player = entry.getKey();
            Duration remainingCooldown = entry.getValue();
            if (remainingCooldown.getSeconds() > 0) {
                remainingCooldowns.put(player, remainingCooldown.minusSeconds(1));
            }
        }
        saveRemainingCooldowns();
    }
}