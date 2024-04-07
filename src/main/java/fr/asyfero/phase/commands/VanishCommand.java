package fr.asyfero.phase.commands;

import fr.asyfero.phase.PointsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VanishCommand implements CommandExecutor, Listener {
    private Set<UUID> vanishedPlayers = new HashSet<>();

    public VanishCommand(PointsManager pointsManager) {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();

        if (player.hasPermission("phase.vanish")) {
            if (vanishedPlayers.contains(playerId)) {
                vanishedPlayers.remove(playerId);
                player.sendMessage(ChatColor.RED + "You n'est plus en Vanish");
                player.setInvisible(false);
                for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                    onlinePlayer.showPlayer(player);
                }
                Bukkit.broadcastMessage(ChatColor.YELLOW + player.getName() + " joined the game");
            } else {
                vanishedPlayers.add(playerId);
                player.sendMessage(ChatColor.RED + "Tu es en Vanish");
                player.setInvisible(true);
                for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                    onlinePlayer.hidePlayer(player);
                }
                Bukkit.broadcastMessage(ChatColor.YELLOW + player.getName() + " left the game");
            }
        } else {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
        }

        return true;
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (vanishedPlayers.contains(player.getUniqueId())) {
            event.setMessage("");
        }
    }
}
