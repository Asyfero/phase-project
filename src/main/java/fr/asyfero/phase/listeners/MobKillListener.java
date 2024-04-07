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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MobKillListener implements Listener {

     private final PointsManager pointsManager;
     private final FileConfiguration dataConfig;
     private final File dataFile;
     private final Map<Player, Integer> mobsKilled = new HashMap<>();

     public MobKillListener(PointsManager pointsManager, JavaPlugin plugin) {
          this.pointsManager = pointsManager;

          dataFile = new File(plugin.getDataFolder(), "data.yml");
          dataConfig = YamlConfiguration.loadConfiguration(dataFile);

          loadMobsKilled();
     }

     @EventHandler
     public void onEntityDeath(EntityDeathEvent event) {
          if (event.getEntity().getKiller() != null) {
               Player player = event.getEntity().getKiller();
               String playerName = player.getName();
               EntityType entity = event.getEntity().getType();

               EntityType entityType = event.getEntityType();
               if (isBreedable(entityType)) {
                    return;
               }

               long mob_value = 0;

               if (entity == EntityType.WITHER) {
                    mob_value = 10;
               } else if (entity == EntityType.WARDEN) {
                    mob_value = 25;
               } else if (entity == EntityType.ELDER_GUARDIAN) {
                    mob_value = 25;
               } else if (entity == EntityType.ENDER_DRAGON) {
                    mob_value = 50;
               } else {
                    mob_value = 1;
               }

               long currentCount = pointsManager.getData(playerName, ".");
               pointsManager.addData(playerName, 1, ".mobskilled");

               long mobsKilledByPlayers = mobsKilled.getOrDefault(player, 0) + mob_value;
               mobsKilled.put(player, (int) mobsKilledByPlayers);

               long remainingMobs = 75 - (mobsKilledByPlayers % 75);

               pointsManager.replaceDataRemain(playerName, remainingMobs, ".mobskilledremain");

               if (mobsKilledByPlayers >= 75) {
                    pointsManager.addData(playerName, 1, ".points");
                    Bukkit.broadcast(Component.text(ChatColor.GOLD + player.getName() + " a obtenu " + ChatColor.RED + 1 + ChatColor.GOLD + " point avec le métier de Chasseur !"));
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);

                    mobsKilled.put(player, 0);
               }

               player.sendActionBar(Component.text(ChatColor.YELLOW + "Chasseur: " + ChatColor.RED + remainingMobs + ChatColor.YELLOW + " / 75"));

               pointsManager.saveFile();
          }
     }

     private boolean isBreedable(EntityType entityType) {
          switch (entityType) {
               case COW:
               case SHEEP:
               case PIG:
               case CHICKEN:
               case HORSE:
               case WOLF:
               case OCELOT:
               case RABBIT:
               case POLAR_BEAR:
               case PLAYER:
                    return true;
               default:
                    return false;
          }
     }

     @EventHandler
     public void onPlayerJoin(PlayerJoinEvent event) {
          Player player = event.getPlayer();
          loadMobsKilled(player);
     }

     @EventHandler
     public void onPlayerQuit(PlayerQuitEvent event) {
          Player player = event.getPlayer();
          pointsManager.saveFile();
          mobsKilled.remove(player);
     }

     private void loadMobsKilled() {
          for (String playerName : dataConfig.getKeys(false)) {
               long mobsKilledCount = pointsManager.getData(playerName, ".mobskilled");
               Player player = Bukkit.getPlayerExact(playerName);
               if (player != null) {
                    mobsKilled.put(player, (int) mobsKilledCount);
               }
          }
     }

     private void loadMobsKilled(Player player) {
          long mobsKilledCount = pointsManager.getData(player.getName(), ".mobskilled");
          mobsKilled.put(player, (int) mobsKilledCount);
     }

     private boolean wasKilledByExplosion(Entity entity) {
          return entity.getLastDamageCause() != null &&
                  (entity.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION ||
                          entity.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION);
     }
}
