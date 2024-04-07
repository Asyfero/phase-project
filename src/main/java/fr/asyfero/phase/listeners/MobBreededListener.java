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
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MobBreededListener implements Listener {

    private final PointsManager pointsManager;
    private final FileConfiguration dataConfig;
    private final File dataFile;
    private final Map<Player, Integer> mobsBreeded = new HashMap<>();

    public MobBreededListener(PointsManager pointsManager, JavaPlugin plugin) {
        this.pointsManager = pointsManager;

        dataFile = new File(plugin.getDataFolder(), "data.yml");
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        loadMobsBreeded();
    }

    @EventHandler
    public void onEntityBreed(EntityBreedEvent event) {
        Player player = (Player) event.getBreeder();
        String playerName = player.getName();

        long currentCount = pointsManager.getData(playerName, ".");
        pointsManager.addData(playerName, 2, ".mobsbreeded");

        long mobsBreededByPlayers = mobsBreeded.getOrDefault(player, 0) + 2;
        mobsBreeded.put(player, (int) mobsBreededByPlayers);

        long remainingMobs = 50 - (mobsBreededByPlayers % 50);

        pointsManager.replaceDataRemain(playerName, remainingMobs, ".mobsbreededremain");

        if (mobsBreededByPlayers >= 50 && mobsBreededByPlayers % 50 == 0) {
            pointsManager.addData(playerName, 1, ".points");
            Bukkit.broadcast(Component.text(ChatColor.GOLD + player.getName() + " a obtenu " + ChatColor.RED + 1 + ChatColor.GOLD + " point avec le métier d'Éleveur !"));
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);

            mobsBreeded.put(player, 0);
        }

        player.sendActionBar(Component.text(ChatColor.YELLOW + "Éleveur: " + ChatColor.RED + remainingMobs + ChatColor.YELLOW + " / 50"));

        pointsManager.saveFile();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        loadmobsBreeded(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        pointsManager.saveFile();
        mobsBreeded.remove(player);
    }

    private void loadMobsBreeded() {
        for (String playerName : dataConfig.getKeys(false)) {
            long mobsBreededCount = pointsManager.getData(playerName, ".mobsbreeded");
            Player player = Bukkit.getPlayerExact(playerName);
            if (player != null) {
                mobsBreeded.put(player, (int) mobsBreededCount);
            }
        }
    }

    private void loadmobsBreeded(Player player) {
        long mobsBreededCount = pointsManager.getData(player.getName(), ".mobsbreeded");
        mobsBreeded.put(player, (int) mobsBreededCount);
    }
}
