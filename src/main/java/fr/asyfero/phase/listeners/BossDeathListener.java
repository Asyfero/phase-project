package fr.asyfero.phase.listeners;

import fr.asyfero.phase.PointsManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class BossDeathListener implements Listener {

    private final PointsManager pointsManager;
    private final File dataFile;
    private final FileConfiguration dataConfig;

    public BossDeathListener(PointsManager pointsManager, Plugin plugin) {
        this.pointsManager = pointsManager;

        dataFile = new File(plugin.getDataFolder(), "boss.yml");
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        loadDragonStatus();
    }

    @EventHandler
    public void onBossDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        EntityDamageEvent lastDamageCause = entity.getLastDamageCause();

        if (event.getEntity().getKiller() != null) {
            Player player = event.getEntity().getKiller();

            if (event.getEntityType() == EntityType.WITHER) {

                boolean whiterDead = loadWitherStatus();

                if (!whiterDead) {
                    pointsManager.addData(player.getName(), 1, ".firstkillwither");
                    Bukkit.broadcast(Component.text(ChatColor.GOLD + player.getName() + " est le premier à tuer un Wither !"));
                    saveWitherStatus(true);
                }

                if (pointsManager.getData(player.getName(), ".whiterkill") != 1) {

                    pointsManager.addData(player.getName(), 1, ".whiterkill");
                    awardPoints(player, 2, ".points", "un Wither !");
                }
            } else if (event.getEntityType() == EntityType.WARDEN) {

                boolean wardenDead = loadWardenStatus();

                if (!wardenDead) {
                    pointsManager.addData(player.getName(), 1, ".firstkillwarden");
                    Bukkit.broadcast(Component.text(ChatColor.GOLD + player.getName() + " est le premier à tuer un Warden !"));
                    saveWardenStatus(true);
                }

                if (pointsManager.getData(player.getName(), ".wardenkill") != 1) {

                    pointsManager.addData(player.getName(), 1, ".wardenkill");
                    awardPoints(player, 3, ".points", "un Warden !");
                }
            } else if (event.getEntityType() == EntityType.ELDER_GUARDIAN) {

                boolean guardianDead = loadGuardianStatus();

                if (!guardianDead) {
                    pointsManager.addData(player.getName(), 1, ".firstkillguardian");
                    Bukkit.broadcast(Component.text(ChatColor.GOLD + player.getName() + " est le premier à tuer un Grand Gardien !"));
                    saveGuardianStatus(true);
                }

                if (pointsManager.getData(player.getName(), ".guardiankill") != 1) {

                    pointsManager.addData(player.getName(), 1, ".guardiankill");
                    awardPoints(player, 3, ".points", "un Grand Gardien !");
                }
            } else if (event.getEntityType() == EntityType.ENDER_DRAGON) {

                boolean enderDragonDead = loadDragonStatus();

                if (!enderDragonDead) {

                    awardPoints(player, 5, ".points", "l'Ender Dragon !");
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
                    }
                    pointsManager.addData(player.getName(), 1, ".dragonkill");
                    saveDragonStatus(true);
                }
            }
        }
    }

    private void awardPoints(Player killer, int value, String type, String mob_type) {
        pointsManager.addData(killer.getName(), value, type);
        Bukkit.broadcast(Component.text(ChatColor.GOLD + killer.getName() + " a obtenu " + ChatColor.RED + value + ChatColor.GOLD + " points en éliminant " + mob_type));
    }

    public boolean loadWitherStatus() {
        if (dataFile.exists()) {
            return dataConfig.getBoolean("FirstWhiterDead", false);
        }
        return false;
    }

    public void saveWitherStatus(boolean witherDead) {
        dataConfig.set("FirstWhiterDead", witherDead);
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean loadWardenStatus() {
        if (dataFile.exists()) {
            return dataConfig.getBoolean("FirstWardenDead", false);
        }
        return false;
    }

    public void saveWardenStatus(boolean wardenDead) {
        dataConfig.set("FirstWardenDead", wardenDead);
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean loadGuardianStatus() {
        if (dataFile.exists()) {
            return dataConfig.getBoolean("FirstGuardianDead", false);
        }
        return false;
    }

    public void saveGuardianStatus(boolean guardianDead) {
        dataConfig.set("FirstGuardianDead", guardianDead);
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean loadDragonStatus() {
        if (dataFile.exists()) {
            return dataConfig.getBoolean("enderDragonDead", false);
        }
        return false;
    }

    public void saveDragonStatus(boolean enderDragonDead) {
        dataConfig.set("EnderDragonDead", enderDragonDead);
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}