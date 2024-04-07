package fr.asyfero.phase;

import fr.asyfero.phase.commands.PointCommand;
import fr.asyfero.phase.commands.TopCommand;
import fr.asyfero.phase.commands.VanishCommand;
import fr.asyfero.phase.listeners.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main extends JavaPlugin {

    private BossBar bossBar;
    private int taskId;
    private int remainingSeconds;
    private PointsManager pointsManager;

    @Override
    public void onEnable() {
        bossBar = Bukkit.createBossBar(ChatColor.GOLD + "Phase d'une Semaine", BarColor.YELLOW, BarStyle.SOLID);
        bossBar.setVisible(true);

        loadProgression();

        if (remainingSeconds <= 0) {

            remainingSeconds = 604800;
        }

        startCountdown();

        pointsManager = new PointsManager(this);

        getServer().getPluginManager().registerEvents(new BlockBreakListener(pointsManager, this), this);
        getServer().getPluginManager().registerEvents(new TopCommand(this, pointsManager), this);
        getServer().getPluginManager().registerEvents(new MobKillListener(pointsManager, this), this);
        getServer().getPluginManager().registerEvents(new MobBreededListener(pointsManager, this), this);
        getServer().getPluginManager().registerEvents(new BlockFarmListener(pointsManager, this), this);
        getServer().getPluginManager().registerEvents(new AdvancementsGetListener(pointsManager, this), this);
        getServer().getPluginManager().registerEvents(new BossDeathListener(pointsManager, this), this);
        getServer().getPluginManager().registerEvents(new PlaytimeTracker(pointsManager, this), this);
        getServer().getPluginManager().registerEvents(new PlayersKilledListener(pointsManager, this), this);
        getCommand("top").setExecutor(new TopCommand(this, pointsManager));
        getCommand("point").setExecutor(new PointCommand(pointsManager));
        getCommand("vanish").setExecutor(new VanishCommand(pointsManager));

        System.out.println("Plugin loaded");
    }

    @Override
    public void onDisable() {
        saveProgression();
        pointsManager.saveFile();
        System.out.println("Plugin unloaded");
    }

    private void startCountdown() {
        Timer timer = new Timer(this, remainingSeconds,
                () -> {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        Bukkit.broadcast(Component.text("Début de la Phase !"));
                        bossBar.addPlayer(player);
                    }
                },
                () -> {
                    Bukkit.broadcast(Component.text("Fin de la Phase !"));

                    Bukkit.shutdown();
                },
                (t) -> {
                    remainingSeconds = t.getSecondsLeft();

                    long days = remainingSeconds / (24 * 3600);
                    long hours = (remainingSeconds % (24 * 3600)) / 3600;
                    long minutes = (remainingSeconds % 3600) / 60;
                    long seconds = remainingSeconds % 60;

                    StringBuilder timeString = new StringBuilder(ChatColor.GOLD + "Fin de la Phase dans " + ChatColor.RED);
                    if (days > 0) {
                        timeString.append(days).append(" j ");
                    }
                    if (hours > 0) {
                        timeString.append(hours).append(" h ");
                    }
                    if (minutes > 0) {
                        timeString.append(minutes).append(" m ");
                    }
                    timeString.append(seconds).append(" s");

                    bossBar.setTitle(timeString.toString().trim());

                    if (remainingSeconds <= 0) {
                        Bukkit.broadcast(Component.text("Fin de la Phase !"));
                        Bukkit.shutdown();
                        Bukkit.getScheduler().cancelTask(taskId);
                    }

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        bossBar.addPlayer(player);
                    }
                }
        );
        timer.scheduleTimer();
    }

    private void loadProgression() {
        try {
            Path file = Paths.get(getDataFolder().toPath().toString(), "countdown_progression.txt");
            if (Files.exists(file)) {
                String content = Files.readString(file);
                remainingSeconds = Integer.parseInt(content.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveProgression() {
        try {
            Path dataFolder = getDataFolder().toPath();
            if (!Files.exists(dataFolder)) {
                Files.createDirectories(dataFolder);
            }
            Path file = Paths.get(dataFolder.toString(), "countdown_progression.txt");
            Files.write(file, String.valueOf(remainingSeconds).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}