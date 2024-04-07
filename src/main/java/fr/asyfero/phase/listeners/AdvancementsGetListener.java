package fr.asyfero.phase.listeners;

import fr.asyfero.phase.PointsManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.advancement.Advancement;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AdvancementsGetListener implements Listener {

    private final PointsManager pointsManager;
    private final FileConfiguration dataConfig;
    private final File dataFile;
    private final Map<Player, Integer> advancementsDone = new HashMap<>();

    public AdvancementsGetListener(PointsManager pointsManager, JavaPlugin plugin) {
        this.pointsManager = pointsManager;

        dataFile = new File(plugin.getDataFolder(), "data.yml");
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        loadAdvancementsDone();
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        Advancement advancement = event.getAdvancement();

        if (isRecipeAdvancement(advancement)) {
            return;
        }

        long currentCount = pointsManager.getData(playerName, ".advancementsdone");

        if (currentCount == 0 && Bukkit.getServer().getPlayerExact(player.getName()) != null) {
            // Reset player's points to 0
            pointsManager.removeData(player.getName(), currentCount, ".advancementsdone");
            // Remove the player's advancement count
            advancementsDone.remove(player);
        }

        pointsManager.addData(playerName, 1, ".advancementsdone");

        long advancementsDoneByPlayer = advancementsDone.getOrDefault(player, 0) + 1;
        advancementsDone.put(player, (int) advancementsDoneByPlayer);

        pointsManager.addData(playerName, 1, ".advancementsdone");
        pointsManager.addData(playerName, 1, ".points");
        Bukkit.broadcast(Component.text(ChatColor.GOLD + player.getName() + " a obtenu " + ChatColor.RED + 1 + ChatColor.GOLD + " point avec le métier d'Aventurier !"));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);

        advancementsDone.put(player, 0);

        player.sendActionBar(Component.text(ChatColor.YELLOW + "Aventurier: " + ChatColor.RED + pointsManager.getData(playerName, ".advancementsdone") + ChatColor.YELLOW + " / 110"));

        if (pointsManager.getData(playerName, ".advancementsdone") == 110) {
            announcement();
            Bukkit.broadcast(Component.text(ChatColor.YELLOW + playerName + " a obtenu tout les achievements ! Il devient inconsidérablement le Maître Aventurier."));
        }

        pointsManager.saveFile();
    }

    private void announcement() {
        for (Player player: Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
        }
    }

    private boolean isRecipeAdvancement(Advancement advancement) {
        String advancementKey = advancement.getKey().getKey();
        return advancementKey.contains("recipe");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        loadAdvancementsDone(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        pointsManager.saveFile();
        advancementsDone.remove(player);
    }

    private void loadAdvancementsDone() {
        for (String playerName : dataConfig.getKeys(false)) {
            long advancementsDoneCount = pointsManager.getData(playerName, ".advancementsdone");
            Player player = Bukkit.getPlayerExact(playerName);
            if (player != null) {
                advancementsDone.put(player, (int) advancementsDoneCount);
            }
        }
    }

    private void loadAdvancementsDone(Player player) {
        long advancementsDoneCount = pointsManager.getData(player.getName(), ".advancementsdone");
        advancementsDone.put(player, (int) advancementsDoneCount);
    }
}
