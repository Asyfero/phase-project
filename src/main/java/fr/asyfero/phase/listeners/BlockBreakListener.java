package fr.asyfero.phase.listeners;

import fr.asyfero.phase.PointsManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
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

public class BlockBreakListener implements Listener {

    private final PointsManager pointsManager;
    private final FileConfiguration dataConfig;
    private final File dataFile;
    private final Map<Player, Integer> blocksBroken = new HashMap<>();

    public BlockBreakListener(PointsManager pointsManager, JavaPlugin plugin) {
        this.pointsManager = pointsManager;

        dataFile = new File(plugin.getDataFolder(), "data.yml");
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        loadBlocksBroken();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        Block block = event.getBlock();
        Material material = block.getType();

        switch (material) {
            case STONE:
            case BLACKSTONE:
            case DEEPSLATE:
            case ANDESITE:
            case DIORITE:
            case GLOWSTONE:
            case COAL_ORE:
            case DEEPSLATE_COAL_ORE:
            case REDSTONE_ORE:
            case DEEPSLATE_REDSTONE_ORE:
            case LAPIS_ORE:
            case DEEPSLATE_LAPIS_ORE:
            case IRON_ORE:
            case DEEPSLATE_IRON_ORE:
            case COPPER_ORE:
            case DEEPSLATE_COPPER_ORE:
            case GOLD_ORE:
            case DEEPSLATE_GOLD_ORE:
            case DIAMOND_ORE:
            case DEEPSLATE_DIAMOND_ORE:
            case EMERALD_ORE:
            case DEEPSLATE_EMERALD_ORE:
            case ANCIENT_DEBRIS:
                long block_value = 0;

                if (material == Material.COAL_ORE) {
                    block_value = 2;
                } else if (material == Material.DEEPSLATE_COAL_ORE) {
                    block_value = 2;
                } else if (material == Material.REDSTONE_ORE) {
                    block_value = 2;
                } else if (material == Material.DEEPSLATE_REDSTONE_ORE) {
                    block_value = 2;
                } else if (material == Material.LAPIS_ORE) {
                    block_value = 2;
                } else if (material == Material.DEEPSLATE_LAPIS_ORE) {
                    block_value = 2;
                } else if (material == Material.IRON_ORE) {
                    block_value = 3;
                } else if (material == Material.DEEPSLATE_IRON_ORE) {
                    block_value = 3;
                } else if (material == Material.COPPER_ORE) {
                    block_value = 3;
                } else if (material == Material.DEEPSLATE_COPPER_ORE) {
                    block_value = 3;
                } else if (material == Material.GOLD_ORE) {
                    block_value = 5;
                } else if (material == Material.DEEPSLATE_GOLD_ORE) {
                    block_value = 5;
                } else if (material == Material.DIAMOND_ORE) {
                    block_value = 10;
                } else if (material == Material.DEEPSLATE_DIAMOND_ORE) {
                    block_value = 10;
                } else if (material == Material.EMERALD_ORE) {
                    block_value = 50;
                } else if (material == Material.DEEPSLATE_EMERALD_ORE) {
                    block_value = 50;
                } else if (material == Material.ANCIENT_DEBRIS) {
                    block_value = 25;
                } else {
                    block_value = 1;
                }

                long currentCount = pointsManager.getData(playerName, ".blocksmined");
                pointsManager.addData(playerName, 1, ".blocksmined");

                long blocksBrokenByPlayer = blocksBroken.getOrDefault(player, 0) + block_value;
                blocksBroken.put(player, (int) blocksBrokenByPlayer);

                long remainingBlocks = 500 - (blocksBrokenByPlayer % 500);

                pointsManager.replaceDataRemain(playerName, remainingBlocks, ".blocksminedremain");

                if (blocksBrokenByPlayer >= 500) {

                    pointsManager.addData(playerName, 1, ".points");
                    Bukkit.broadcast(Component.text(ChatColor.GOLD + player.getName() + " a obtenu " + ChatColor.RED + 1 + ChatColor.GOLD + " point avec le métier de Mineur !"));
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);

                    blocksBroken.put(player, 0);
                }

                player.sendActionBar(Component.text(ChatColor.YELLOW + "Mineur: " + ChatColor.RED + remainingBlocks + ChatColor.YELLOW + " / 500"));

                pointsManager.saveFile();
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        loadBlocksBroken(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        pointsManager.saveFile();
        blocksBroken.remove(player);
    }

    private void loadBlocksBroken() {

        for (String playerName : dataConfig.getKeys(false)) {
            long blocksBrokenCount = pointsManager.getData(playerName, ".blocksmined");
            Player player = Bukkit.getPlayerExact(playerName);
            if (player != null) {
                blocksBroken.put(player, (int) blocksBrokenCount);
            }
        }
    }

    private void loadBlocksBroken(Player player) {

        long blocksBrokenCount = pointsManager.getData(player.getName(), ".blocksmined");
        blocksBroken.put(player, (int) blocksBrokenCount);
    }
}