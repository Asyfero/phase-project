package fr.asyfero.phase.commands;

import fr.asyfero.phase.PointsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class TopCommand implements CommandExecutor, Listener {

    private final PointsManager pointsManager;
    private final JavaPlugin plugin;

    public TopCommand(JavaPlugin plugin, PointsManager pointsManager) {
        this.plugin = plugin;
        this.pointsManager = pointsManager;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return false;
        }

        Player player = (Player) sender;
        openMenuGui(player);
        return true;
    }

    private ItemStack createCrossItem() {
        ItemStack crossItem = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta crossMeta = crossItem.getItemMeta();
        crossMeta.setDisplayName(ChatColor.RED + "Retour au Menu");
        crossItem.setItemMeta(crossMeta);
        return crossItem;
    }

    private void openMenuGui(Player player) {
        Inventory topGui = Bukkit.createInventory(null, 9, ChatColor.YELLOW + "Minecraft Phase");

        ItemStack grassBlock = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta grassMeta = grassBlock.getItemMeta();
        grassMeta.setDisplayName(ChatColor.GREEN + "Classement Métiers");
        grassBlock.setItemMeta(grassMeta);

        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(player.getName()));
        skullMeta.setDisplayName(ChatColor.GRAY + "Tes Stats");
        playerHead.setItemMeta(skullMeta);

        ItemStack goldBlock = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta goldMeta = goldBlock.getItemMeta();
        goldMeta.setDisplayName(ChatColor.GOLD + "Classement Points");
        goldBlock.setItemMeta(goldMeta);

        topGui.setItem(1, grassBlock);
        topGui.setItem(4, playerHead);
        topGui.setItem(7, goldBlock);

        player.openInventory(topGui);
    }

    private void openTopPlayersGui(Player player) {
        Inventory topPlayersGui = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Classement Points");

        List<String> topPlayers = pointsManager.getTopPlayers(3, ".points");
        int middleSlot = 13;
        int leftSlot = 21;
        int rightSlot = 23;

        for (int i = 0; i < topPlayers.size(); i++) {
            String playerName = topPlayers.get(i);
            long points = pointsManager.getData(playerName, ".points");

            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(playerName));
            skullMeta.setDisplayName(ChatColor.YELLOW + playerName + " - " + ChatColor.RED + points + ChatColor.YELLOW + " points");
            playerHead.setItemMeta(skullMeta);

            if (i == 0) {
                topPlayersGui.setItem(middleSlot - 9, playerHead);
            } else if (i == 1) {
                topPlayersGui.setItem(leftSlot - 9, playerHead);
            } else if (i == 2) {
                topPlayersGui.setItem(rightSlot - 9, playerHead);
            }
        }

        ItemStack bedrockBlock = new ItemStack(Material.BEDROCK);
        ItemMeta bedrockMeta = bedrockBlock.getItemMeta();
        bedrockMeta.setDisplayName(ChatColor.YELLOW + "1ère Place");
        bedrockBlock.setItemMeta(bedrockMeta);
        topPlayersGui.setItem(middleSlot, bedrockBlock);

        ItemStack diamondBlock = new ItemStack(Material.DIAMOND_BLOCK);
        ItemMeta diamondMeta = diamondBlock.getItemMeta();
        diamondMeta.setDisplayName(ChatColor.GOLD + "Seconde Place");
        diamondBlock.setItemMeta(diamondMeta);
        topPlayersGui.setItem(leftSlot, diamondBlock);

        ItemStack goldBlock = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta goldMeta = goldBlock.getItemMeta();
        goldMeta.setDisplayName(ChatColor.GRAY + "Troisième Place");
        goldBlock.setItemMeta(goldMeta);
        topPlayersGui.setItem(rightSlot, goldBlock);

        ItemStack cross = createCrossItem();
        topPlayersGui.setItem(26, cross);

        player.openInventory(topPlayersGui);
    }

    private void openClassGui(Player player) {
        Inventory classGUI = Bukkit.createInventory(null, 54, ChatColor.GREEN + "Classement Métiers");

        List<String> topPlayerMiner = pointsManager.getTopPlayers(1, ".blocksmined");
        if (!topPlayerMiner.isEmpty()) {
            String topPlayerMinerName = topPlayerMiner.get(0);
            long topPlayerMinerBlocks = pointsManager.getData(topPlayerMinerName, ".blocksmined");
            ItemStack playerHeadMiner = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMetaMiner = (SkullMeta) playerHeadMiner.getItemMeta();
            skullMetaMiner.setOwningPlayer(Bukkit.getOfflinePlayer(topPlayerMinerName));
            skullMetaMiner.setDisplayName(ChatColor.YELLOW + topPlayerMinerName + ChatColor.GOLD + " - " + ChatColor.RED + topPlayerMinerBlocks + ChatColor.GOLD + " blocks minés");
            playerHeadMiner.setItemMeta(skullMetaMiner);
            classGUI.setItem(1, playerHeadMiner);
        }

        ItemStack stonePickaxe = new ItemStack(Material.STONE_PICKAXE);
        ItemMeta pickaxeMeta = stonePickaxe.getItemMeta();
        pickaxeMeta.setDisplayName(ChatColor.GOLD + "Maître Mineur");
        stonePickaxe.setItemMeta(pickaxeMeta);

        List<String> topPlayerHunter = pointsManager.getTopPlayers(1, ".mobskilled");
        if (!topPlayerHunter.isEmpty()) {
            String topPlayerHunterName = topPlayerHunter.get(0);
            long topPlayerHunterMobs = pointsManager.getData(topPlayerHunterName, ".mobskilled");
            ItemStack playerHeadHunter = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMetaHunter = (SkullMeta) playerHeadHunter.getItemMeta();
            skullMetaHunter.setOwningPlayer(Bukkit.getOfflinePlayer(topPlayerHunterName));
            skullMetaHunter.setDisplayName(ChatColor.YELLOW + topPlayerHunterName + ChatColor.GOLD + " - " + ChatColor.RED + topPlayerHunterMobs + ChatColor.GOLD + " mobs tués");
            playerHeadHunter.setItemMeta(skullMetaHunter);
            classGUI.setItem(3, playerHeadHunter);
        }

        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta bowMeta = bow.getItemMeta();
        bowMeta.setDisplayName(ChatColor.GOLD + "Maître Chasseur");
        bow.setItemMeta(bowMeta);

        List<String> topPlayerBreeder = pointsManager.getTopPlayers(1, ".mobsbreeded");
        if (!topPlayerBreeder.isEmpty()) {
            String topPlayerBreederName = topPlayerBreeder.get(0);
            long topPlayerBreederMobs = pointsManager.getData(topPlayerBreederName, ".mobsbreeded");
            ItemStack playerHeadBreeder = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMetaBreeder = (SkullMeta) playerHeadBreeder.getItemMeta();
            skullMetaBreeder.setOwningPlayer(Bukkit.getOfflinePlayer(topPlayerBreederName));
            skullMetaBreeder.setDisplayName(ChatColor.YELLOW + topPlayerBreederName + ChatColor.GOLD + " - " + ChatColor.RED + topPlayerBreederMobs + ChatColor.GOLD + " mobs nourris");
            playerHeadBreeder.setItemMeta(skullMetaBreeder);
            classGUI.setItem(5, playerHeadBreeder);
        }

        ItemStack lead = new ItemStack(Material.LEAD);
        ItemMeta leadMeta = lead.getItemMeta();
        leadMeta.setDisplayName(ChatColor.GOLD + "Maître Éleveur");
        lead.setItemMeta(leadMeta);

        List<String> topPlayerFarmer = pointsManager.getTopPlayers(1, ".blocksfarmed");
        if (!topPlayerFarmer.isEmpty()) {
            String topPlayerFarmerName = topPlayerFarmer.get(0);
            long topPlayerFarmerBlocks = pointsManager.getData(topPlayerFarmerName, ".blocksfarmed");
            ItemStack playerHeadFarmer = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMetaFarmer = (SkullMeta) playerHeadFarmer.getItemMeta();
            skullMetaFarmer.setOwningPlayer(Bukkit.getOfflinePlayer(topPlayerFarmerName));
            skullMetaFarmer.setDisplayName(ChatColor.YELLOW + topPlayerFarmerName + ChatColor.GOLD + " - " + ChatColor.RED + topPlayerFarmerBlocks + ChatColor.GOLD + " plantations récoltées");
            playerHeadFarmer.setItemMeta(skullMetaFarmer);
            classGUI.setItem(7, playerHeadFarmer);
        }

        ItemStack hoe = new ItemStack(Material.WOODEN_HOE);
        ItemMeta hoeMeta = hoe.getItemMeta();
        hoeMeta.setDisplayName(ChatColor.GOLD + "Maître Fermier");
        hoe.setItemMeta(hoeMeta);

        List<String> topPlayerAdvancement = pointsManager.getTopPlayers(1, ".advancementsdone");
        if (!topPlayerAdvancement.isEmpty()) {
            String topPlayerAdvancementName = topPlayerAdvancement.get(0);
            long topPlayerAdvancementDone = pointsManager.getData(topPlayerAdvancementName, ".advancementsdone");
            ItemStack playerHeadAdvancement = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMetaAdvancement = (SkullMeta) playerHeadAdvancement.getItemMeta();
            skullMetaAdvancement.setOwningPlayer(Bukkit.getOfflinePlayer(topPlayerAdvancementName));
            skullMetaAdvancement.setDisplayName(ChatColor.YELLOW + topPlayerAdvancementName + ChatColor.GOLD + " - " + ChatColor.RED + topPlayerAdvancementDone + ChatColor.GOLD + " achievements réalisés");
            playerHeadAdvancement.setItemMeta(skullMetaAdvancement);
            classGUI.setItem(18, playerHeadAdvancement);
        }

        ItemStack grass = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta grassMeta = grass.getItemMeta();
        grassMeta.setDisplayName(ChatColor.GOLD + "Maître Aventurier");
        grass.setItemMeta(grassMeta);

        List<String> topPlayerPlaytime = pointsManager.getTopPlayers(1, ".playtime");
        if (!topPlayerPlaytime.isEmpty()) {
            String topPlayerPlaytimeName = topPlayerPlaytime.get(0);
            long topPlayerPlaytimeDone = pointsManager.getData(topPlayerPlaytimeName, ".playtime");
            ItemStack playerHeadPlaytime = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMetaPlaytime = (SkullMeta) playerHeadPlaytime.getItemMeta();
            skullMetaPlaytime.setOwningPlayer(Bukkit.getOfflinePlayer(topPlayerPlaytimeName));
            skullMetaPlaytime.setDisplayName(ChatColor.YELLOW + topPlayerPlaytimeName + ChatColor.GOLD + " - " + "a joué durant " + ChatColor.RED + formatPlaytime(topPlayerPlaytimeDone));
            playerHeadPlaytime.setItemMeta(skullMetaPlaytime);
            classGUI.setItem(26, playerHeadPlaytime);
        }

        ItemStack clock = new ItemStack(Material.CLOCK);
        ItemMeta clockMeta = clock.getItemMeta();
        clockMeta.setDisplayName(ChatColor.GOLD + "Maître Horloger");
        clock.setItemMeta(clockMeta);

        List<String> topPlayerKill = pointsManager.getTopPlayers(1, ".playerskilled");
        if (!topPlayerKill.isEmpty()) {
            String topPlayerKillName = topPlayerKill.get(0);
            long topPlayerKillDone = pointsManager.getData(topPlayerKillName, ".playerskilled");
            ItemStack playerHeadKill = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMetaKill = (SkullMeta) playerHeadKill.getItemMeta();
            skullMetaKill.setOwningPlayer(Bukkit.getOfflinePlayer(topPlayerKillName));
            skullMetaKill.setDisplayName(ChatColor.YELLOW + topPlayerKillName + ChatColor.GOLD + " - " + "à tués " + ChatColor.RED + topPlayerKillDone + ChatColor.GOLD + " joueurs");
            playerHeadKill.setItemMeta(skullMetaKill);
            classGUI.setItem(22, playerHeadKill);
        }

        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setDisplayName(ChatColor.GOLD + "Maître Assassin");
        sword.setItemMeta(swordMeta);

        List<String> topPlayerWither = pointsManager.getTopPlayers(1, ".firstkillwither");
        if (!topPlayerWither.isEmpty()) {
            String topPlayerWitherName = topPlayerWither.get(0);
            ItemStack playerHeadWither = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMetaWither = (SkullMeta) playerHeadWither.getItemMeta();
            skullMetaWither.setOwningPlayer(Bukkit.getOfflinePlayer(topPlayerWitherName));
            skullMetaWither.setDisplayName(ChatColor.YELLOW + topPlayerWitherName + ChatColor.GOLD + " - a tué le premier Wither");
            playerHeadWither.setItemMeta(skullMetaWither);
            classGUI.setItem(37, playerHeadWither);
        }

        ItemStack witherSkull = new ItemStack(Material.WITHER_SKELETON_SKULL);
        ItemMeta witherSkullMeta = witherSkull.getItemMeta();
        witherSkullMeta.setDisplayName(ChatColor.GOLD + "Tueur de Wither");
        witherSkull.setItemMeta(witherSkullMeta);

        List<String> topPlayerWarden = pointsManager.getTopPlayers(1, ".firstkillwarden");
        if (!topPlayerWarden.isEmpty()) {
            String topPlayerWardenName = topPlayerWarden.get(0);
            ItemStack playerHeadWarden = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMetaWarden = (SkullMeta) playerHeadWarden.getItemMeta();
            skullMetaWarden.setOwningPlayer(Bukkit.getOfflinePlayer(topPlayerWardenName));
            skullMetaWarden.setDisplayName(ChatColor.YELLOW + topPlayerWardenName + ChatColor.GOLD + " - a tué le premier Warden");
            playerHeadWarden.setItemMeta(skullMetaWarden);
            classGUI.setItem(39, playerHeadWarden);
        }

        ItemStack wardenSkull = new ItemStack(Material.SCULK_SHRIEKER);
        ItemMeta wardenSkullMeta = wardenSkull.getItemMeta();
        wardenSkullMeta.setDisplayName(ChatColor.GOLD + "Tueur de Warden");
        wardenSkull.setItemMeta(wardenSkullMeta);

        List<String> topPlayerGuardian = pointsManager.getTopPlayers(1, ".firstkillguardian");
        if (!topPlayerGuardian.isEmpty()) {
            String topPlayerGuardianName = topPlayerGuardian.get(0);
            ItemStack playerHeadGuardian = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMetaGuardian = (SkullMeta) playerHeadGuardian.getItemMeta();
            skullMetaGuardian.setOwningPlayer(Bukkit.getOfflinePlayer(topPlayerGuardianName));
            skullMetaGuardian.setDisplayName(ChatColor.YELLOW + topPlayerGuardianName + ChatColor.GOLD + " - a tué le premier Grand Gardien !");
            playerHeadGuardian.setItemMeta(skullMetaGuardian);
            classGUI.setItem(41, playerHeadGuardian);
        }

        ItemStack guardianSkull = new ItemStack(Material.PRISMARINE);
        ItemMeta guardianSkullMeta = guardianSkull.getItemMeta();
        guardianSkullMeta.setDisplayName(ChatColor.GOLD + "Tueur de Grand Gardien");
        guardianSkull.setItemMeta(guardianSkullMeta);

        List<String> topPlayerDragon = pointsManager.getTopPlayers(1, ".dragonkill");
        if (!topPlayerDragon.isEmpty()) {
            String topPlayerDragonName = topPlayerDragon.get(0);
            ItemStack playerHeadDragon = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMetaDragon = (SkullMeta) playerHeadDragon.getItemMeta();
            skullMetaDragon.setOwningPlayer(Bukkit.getOfflinePlayer(topPlayerDragonName));
            skullMetaDragon.setDisplayName(ChatColor.YELLOW + topPlayerDragonName + ChatColor.GOLD + " - à tué le Dragon de l'End");
            playerHeadDragon.setItemMeta(skullMetaDragon);
            classGUI.setItem(43, playerHeadDragon);
        }

        ItemStack dragonSkull = new ItemStack(Material.DRAGON_HEAD);
        ItemMeta dragonSkullMeta = dragonSkull.getItemMeta();
        dragonSkullMeta.setDisplayName(ChatColor.GOLD + "Tueur de Dragon");
        dragonSkull.setItemMeta(dragonSkullMeta);

        classGUI.setItem(10, stonePickaxe);
        classGUI.setItem(12, bow);
        classGUI.setItem(14, lead);
        classGUI.setItem(16, hoe);

        classGUI.setItem(27, grass);
        classGUI.setItem(35, clock);
        classGUI.setItem(31, sword);

        classGUI.setItem(46, witherSkull);
        classGUI.setItem(48, wardenSkull);
        classGUI.setItem(50, guardianSkull);
        classGUI.setItem(52, dragonSkull);

        ItemStack cross = createCrossItem();
        classGUI.setItem(53, cross);

        player.openInventory(classGUI);
    }

    private void openStatsGui(Player player) {
        Inventory statsGui = Bukkit.createInventory(null, 54, ChatColor.GRAY + "Tes Stats");

        String topPlayerMinerName = player.getName();
        long playerMinerBlocks = pointsManager.getData(topPlayerMinerName, ".blocksmined");
        ItemStack stonePickaxe = new ItemStack(Material.STONE_PICKAXE);
        ItemMeta pickaxeMeta = stonePickaxe.getItemMeta();
        pickaxeMeta.setDisplayName("" + ChatColor.RED + playerMinerBlocks + ChatColor.GOLD + " blocks minés");
        stonePickaxe.setItemMeta(pickaxeMeta);

        String topPlayerHunterName = player.getName();
        long playerHunterMobs = pointsManager.getData(topPlayerHunterName, ".mobskilled");
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta bowMeta = bow.getItemMeta();
        bowMeta.setDisplayName("" + ChatColor.RED + playerHunterMobs + ChatColor.GOLD + " mobs tués");
        bow.setItemMeta(bowMeta);

        String topPlayerBreederName = player.getName();
        long playerBreederMobs = pointsManager.getData(topPlayerBreederName, ".mobsbreeded");
        ItemStack lead = new ItemStack(Material.LEAD);
        ItemMeta leadMeta = lead.getItemMeta();
        leadMeta.setDisplayName("" + ChatColor.RED + playerBreederMobs + ChatColor.GOLD + " mobs nourris");
        lead.setItemMeta(leadMeta);

        String topPlayerFarmerName = player.getName();
        long playerFarmerBlocks = pointsManager.getData(topPlayerFarmerName, ".blocksfarmed");
        ItemStack hoe = new ItemStack(Material.WOODEN_HOE);
        ItemMeta hoeMeta = hoe.getItemMeta();
        hoeMeta.setDisplayName("" + ChatColor.RED + playerFarmerBlocks + ChatColor.GOLD + " plantations récoltées");
        hoe.setItemMeta(hoeMeta);

        String topPlayerAdvancementName = player.getName();
        long playerAdvancements = pointsManager.getData(topPlayerAdvancementName, ".advancementsdone");
        ItemStack grass = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta grassMeta = grass.getItemMeta();
        grassMeta.setDisplayName("" + ChatColor.RED + playerAdvancements + ChatColor.GOLD + " achievements réalisés");
        grass.setItemMeta(grassMeta);

        String topPlayerKillerName = player.getName();
        long playerKiller = pointsManager.getData(topPlayerKillerName, ".playerskilled");
        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta swordMeta = grass.getItemMeta();
        swordMeta.setDisplayName("" + ChatColor.RED + playerKiller + ChatColor.GOLD + " joueurs tués");
        sword.setItemMeta(swordMeta);

        String topPlayerPlaytimeName = player.getName();
        long playerPlaytime = pointsManager.getData(topPlayerPlaytimeName, ".playtime");
        ItemStack clock = new ItemStack(Material.CLOCK);
        ItemMeta clockMeta = clock.getItemMeta();
        clockMeta.setDisplayName(ChatColor.GOLD + "Tu as joué durant " + ChatColor.RED + formatPlaytime(playerPlaytime));
        clock.setItemMeta(clockMeta);

        if (pointsManager.getData(player.getName(), ".firstkillwither") == 1) {
            ItemStack witherSkull = new ItemStack(Material.WITHER_SKELETON_SKULL);
            ItemMeta witherSkullMeta = witherSkull.getItemMeta();
            witherSkullMeta.setDisplayName(ChatColor.GOLD + "Tu as été le premier à tuer un Wither");
            witherSkull.setItemMeta(witherSkullMeta);
            statsGui.setItem(46, witherSkull);
        }

        if (pointsManager.getData(player.getName(), ".firstkillwarden") == 1) {
            ItemStack wardenSkull = new ItemStack(Material.SCULK_SENSOR);
            ItemMeta wardenSkullMeta = wardenSkull.getItemMeta();
            wardenSkullMeta.setDisplayName(ChatColor.GOLD + "Tu as été le premier à tuer un Warden");
            wardenSkull.setItemMeta(wardenSkullMeta);
            statsGui.setItem(48, wardenSkull);
        }

        if (pointsManager.getData(player.getName(), ".firstkillguardian") == 1) {
            ItemStack guardianSkull = new ItemStack(Material.PRISMARINE);
            ItemMeta guardianSkullMeta = guardianSkull.getItemMeta();
            guardianSkullMeta.setDisplayName(ChatColor.GOLD + "Tu as été le premier à tuer un Grand Gardien");
            guardianSkull.setItemMeta(guardianSkullMeta);
            statsGui.setItem(50, guardianSkull);
        }

        if (pointsManager.getData(player.getName(), ".dragonkill") == 1) {
            ItemStack dragonSkull = new ItemStack(Material.DRAGON_HEAD);
            ItemMeta dragonSkullMeta = dragonSkull.getItemMeta();
            dragonSkullMeta.setDisplayName(ChatColor.GOLD + "Tu as été le premier à tuer l'Ender Dragon");
            dragonSkull.setItemMeta(dragonSkullMeta);
            statsGui.setItem(52, dragonSkull);
        }

        statsGui.setItem(10, stonePickaxe);
        statsGui.setItem(12, bow);
        statsGui.setItem(14, lead);
        statsGui.setItem(16, hoe);

        statsGui.setItem(27, grass);
        statsGui.setItem(35, clock);
        statsGui.setItem(31, sword);

        ItemStack cross = createCrossItem();
        statsGui.setItem(53, cross);
        player.openInventory(statsGui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        String inventoryTitle = event.getView().getTitle();
        if (inventoryTitle.equals(ChatColor.YELLOW + "Minecraft Phase")) {
            event.setCancelled(true);
            if (event.getRawSlot() == 1) {
                openClassGui(player);
            } else if (event.getRawSlot() == 4) {
                openStatsGui(player);
            } else if (event.getRawSlot() == 7) {
                openTopPlayersGui(player);
            }
        } else if (inventoryTitle.equals(ChatColor.GOLD + "Classement Points")) {
            event.setCancelled(true);
            int clickedSlot = event.getRawSlot();
            if (clickedSlot == 26) {
                openMenuGui(player);
            }
        } else if (inventoryTitle.equals(ChatColor.GREEN + "Classement Métiers")) {
            event.setCancelled(true);
            int clickedSlot = event.getRawSlot();
            if (clickedSlot == 53) {
                openMenuGui(player);
            }
        } else if (inventoryTitle.equals(ChatColor.GRAY + "Tes Stats")) {
            event.setCancelled(true);
            int clickedSlot = event.getRawSlot();
            if (clickedSlot == 53) {
                openMenuGui(player);
            }
        }
    }

    private String formatPlaytime(long seconds) {

        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;

        return String.format(hours + " h " + minutes % 60 + " m " + seconds % 60 + " s ");
    }
}