package fr.asyfero.phase.listeners;

import fr.asyfero.phase.PointsManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class BlockFarmListener implements Listener {

    private final PointsManager pointsManager;
    private final FileConfiguration dataConfig;
    private final File dataFile;
    private final Map<Player, Integer> blocksFarmed = new HashMap<>();

    public BlockFarmListener(PointsManager pointsManager, JavaPlugin plugin) {
        this.pointsManager = pointsManager;

        dataFile = new File(plugin.getDataFolder(), "data.yml");
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        loadBlocksFarmed();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        Block block = event.getBlock();
        Material material = block.getType();

        switch (material) {
            case WHEAT:
            case CARROTS:
            case POTATOES:
            case BEETROOTS:
                if (block.getBlockData() instanceof Ageable) {
                    Ageable crop = (Ageable) block.getBlockData();
                    if (crop.getAge() == crop.getMaximumAge()) {

                        long currentCount = pointsManager.getData(playerName, ".blocksfarmed");
                        pointsManager.addData(playerName, 1, ".blocksfarmed");

                        long blocksFarmedByPlayer = blocksFarmed.getOrDefault(player, 0) + 1;
                        blocksFarmed.put(player, (int) blocksFarmedByPlayer);

                        long remainingBlocks = 100 - (blocksFarmedByPlayer % 100);

                        pointsManager.replaceDataRemain(playerName, remainingBlocks, ".blocksfarmedremain");

                        if (blocksFarmedByPlayer >= 100 && blocksFarmedByPlayer % 100 == 0) {
                            pointsManager.addData(playerName, 1, ".blocksfarmed");
                            Bukkit.broadcast(Component.text(ChatColor.GOLD + player.getName() + " a obtenu " + ChatColor.RED + 1 + ChatColor.GOLD + " point avec le métier de Fermier !"));
                            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);

                            blocksFarmed.put(player, 0);
                        }

                        player.sendActionBar(Component.text(ChatColor.YELLOW + "Fermier: " + ChatColor.RED + remainingBlocks + ChatColor.YELLOW + " / 100"));

                        pointsManager.saveFile();
                    }
                }
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        loadBlocksFarmed(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        pointsManager.saveFile();
        blocksFarmed.remove(player);
    }

    private void loadBlocksFarmed() {
        for (String playerName : dataConfig.getKeys(false)) {
            long blocksFarmedCount = pointsManager.getData(playerName, ".blocksfarmed");
            Player player = Bukkit.getPlayerExact(playerName);
            if (player != null) {
                blocksFarmed.put(player, (int) blocksFarmedCount);
            }
        }
    }

    private void loadBlocksFarmed(Player player) {
        long blocksFarmedCount = pointsManager.getData(player.getName(), ".blocksfarmed");
        blocksFarmed.put(player, (int) blocksFarmedCount);
    }
}
