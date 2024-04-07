package fr.asyfero.phase;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PointsManager {

    private final File dataFile;
    private final FileConfiguration dataConfig;

    public PointsManager(JavaPlugin plugin) {
        dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public List<String> getTopPlayers(long limit, String type) {
        List<String> allPlayers = new ArrayList<>(dataConfig.getKeys(false));
        allPlayers.sort(Comparator.comparingInt(playerName -> -dataConfig.getInt(playerName + type, 0)));
        return allPlayers.stream().limit(limit).collect(Collectors.toList());
    }

    public void addData(String playerName, long points, String type) {
        long currentPoints = getData(playerName, type);
        dataConfig.set(playerName + type, currentPoints + points);
        saveFile();
    }

    public long getData(String playerName, String type) {
        return dataConfig.getLong(playerName + type, 0);
    }

    public void replaceDataRemain(String playerName, long points, String type) {
        dataConfig.set(playerName + type, points);
        saveFile();
    }

    public void removeData(String playerName, long points, String type) {
        long currentPoints = getData(playerName, type);
        dataConfig.set(playerName + type, currentPoints - points);
        saveFile();
    }

    public void saveFile() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
