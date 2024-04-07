package fr.asyfero.phase.commands;

import fr.asyfero.phase.PointsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PointCommand implements CommandExecutor {

    private final PointsManager pointsManager;
    public PointCommand(PointsManager pointsManager) {
        this.pointsManager = pointsManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) || !sender.hasPermission("phase.admin")) {
            sender.sendMessage("You don't have permission to use this command.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("Usage: /point <add/get/remove> <player> <number> <type>");
            sender.sendMessage("type: points, miner, hunter, breeder, advancements, farmer, assassin");
            return true;
        }

        Player targetPlayer = sender.getServer().getPlayer(args[1]);
        if (targetPlayer == null) {
            sender.sendMessage("Player not found.");
            return true;
        }

        int points;
        try {
            points = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid number format.");
            return true;
        }

        String action_0 = args[0].toLowerCase();
        String action_3 = args[3].toLowerCase();
        if (action_0.equals("add")) {
            switch (action_3) {
                case "points":
                    pointsManager.addData(targetPlayer.getName(), points, ".points");
                    sender.sendMessage(points + " points ajouté(s) à " + targetPlayer);
                    break;
                case "miner":
                    pointsManager.addData(targetPlayer.getName(), points, "blocksmined");
                    sender.sendMessage(points + " points ajouté(s) à " + targetPlayer);
                    break;
                case "hunter":
                    pointsManager.addData(targetPlayer.getName(), points, ".mobskilled");
                    sender.sendMessage(points + " points ajouté(s) à " + targetPlayer);
                    break;
                case "breeder":
                    pointsManager.addData(targetPlayer.getName(), points, ".mobsbreeded");
                    sender.sendMessage(points + " points ajouté(s) à " + targetPlayer);
                    break;
                case "advancements":
                    pointsManager.addData(targetPlayer.getName(), points, ".advancementsdone");
                    sender.sendMessage(points + " points ajouté(s) à " + targetPlayer);
                    break;
                case "farmer":
                    pointsManager.addData(targetPlayer.getName(), points, ".blocksfarmed");
                    sender.sendMessage(points + " points ajouté(s) à " + targetPlayer);
                    break;
                case "assassin":
                    pointsManager.addData(targetPlayer.getName(), points, ".playerskilled");
                    sender.sendMessage(points + " points ajouté(s) à " + targetPlayer);
                    break;
                default:
                    break;
            }
        } else if (action_0.equals("get")) {
            switch (action_3) {
                case "points":
                    pointsManager.getData(targetPlayer.getName(),".points");
                    sender.sendMessage(targetPlayer +  " a " + points + " points dans ce type");
                    break;
                case "miner":
                    pointsManager.getData(targetPlayer.getName(),"blocksmined");
                    sender.sendMessage(targetPlayer +  " a " + points + " points dans ce type");
                    break;
                case "hunter":
                    pointsManager.getData(targetPlayer.getName(),".mobskilled");
                    sender.sendMessage(targetPlayer +  " a " + points + " points dans ce type");
                    break;
                case "breeder":
                    pointsManager.getData(targetPlayer.getName(), ".mobsbreeded");
                    sender.sendMessage(targetPlayer +  " a " + points + " points dans ce type");
                    break;
                case "advancements":
                    pointsManager.getData(targetPlayer.getName(),".advancementsdone");
                    sender.sendMessage(targetPlayer +  " a " + points + " points dans ce type");
                    break;
                case "farmer":
                    pointsManager.getData(targetPlayer.getName(),".blocksfarmed");
                    sender.sendMessage(targetPlayer +  " a " + points + " points dans ce type");
                    break;
                case "assassin":
                    pointsManager.getData(targetPlayer.getName(),".playerskilled");
                    sender.sendMessage(targetPlayer +  " a " + points + " points dans ce type");
                    break;
                default:
                    break;
            }
        } else if (action_0.equals("remove")) {
            switch (action_3) {
                case "points":
                    pointsManager.removeData(targetPlayer.getName(), points, ".points");
                    sender.sendMessage(points + " points retirés(s) à " + targetPlayer);
                    break;
                case "miner":
                    pointsManager.removeData(targetPlayer.getName(), points, "blocksmined");
                    sender.sendMessage(points + " points retirés(s) à " + targetPlayer);
                    break;
                case "hunter":
                    pointsManager.removeData(targetPlayer.getName(), points, ".mobskilled");
                    sender.sendMessage(points + " points retirés(s) à " + targetPlayer);
                    break;
                case "breeder":
                    pointsManager.removeData(targetPlayer.getName(), points, ".mobsbreeded");
                    sender.sendMessage(points + " points retirés(s) à " + targetPlayer);
                    break;
                case "advancements":
                    pointsManager.removeData(targetPlayer.getName(), points, ".advancementsdone");
                    sender.sendMessage(points + " points retirés(s) à " + targetPlayer);
                    break;
                case "farmer":
                    pointsManager.removeData(targetPlayer.getName(), points, ".blocksfarmed");
                    sender.sendMessage(points + " points retirés(s) à " + targetPlayer);
                    break;
                case "assassin":
                    pointsManager.removeData(targetPlayer.getName(), points, ".playerskilled");
                    sender.sendMessage(points + " points retirés(s) à " + targetPlayer);
                    break;
                default:
                    break;
            }
        } else {
            sender.sendMessage("Action invalide, utilisez add / get / remove");
        }

        return true;
    }
}
