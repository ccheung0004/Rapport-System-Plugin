package me.crunchycars.rapportSystem;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RapportSystem extends JavaPlugin implements Listener, CommandExecutor {

    private final Map<String, Map<UUID, Integer>> interactionCounts = new HashMap<>();
    private NPC npcPlayer;
    private File dataFile;
    private FileConfiguration dataConfig;

    @Override
    public void onEnable() {
        loadDataFile(); // Ensure data.yml is loaded or created
        getServer().getPluginManager().registerEvents(this, this);
        loadNPCById(20); // Replace with your NPC's ID
        loadInteractionCounts(); // Load interaction counts from file
        getLogger().info("RapportSystem plugin enabled.");

        // Register commands
        getCommand("giveAmyRapportItem").setExecutor(this);
        getCommand("giveJakeRapportItem").setExecutor(this);
        getCommand("giveMatheoRapportItem").setExecutor(this);
        getCommand("giveDmitryRapportItem").setExecutor(this);
        getCommand("rapport").setExecutor(this);  
    }

    @Override
    public void onDisable() {
        saveInteractionCounts(); // Save interaction counts to file
        getLogger().info("RapportSystem plugin disabled.");
    }

    public void loadNPCById(int npcId) {
        npcPlayer = CitizensAPI.getNPCRegistry().getById(npcId);
        if (npcPlayer != null) {
            getLogger().info("NPC loaded with ID: " + npcId);
        } else {
            getLogger().warning("NPC with ID " + npcId + " not found!");
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        getLogger().info("PlayerInteractEntityEvent triggered.");

        // Ensure the entity clicked is our NPC
        if (npcPlayer != null && event.getRightClicked().equals(npcPlayer.getEntity())) {
            getLogger().info("Rapport NPC was clicked!");

            Player player = event.getPlayer();
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand != null && itemInHand.hasItemMeta()) {
                ItemMeta meta = itemInHand.getItemMeta();
                if (meta != null && meta.hasDisplayName()) {
                    String displayName = meta.getDisplayName();
                    getLogger().info("Player is holding an item with display name: " + displayName);

                    // Define the reward thresholds and corresponding commands for Amy's Rapport Ticket
                    Map<Integer, String> amyRewardCommands = new HashMap<>();
                    amyRewardCommands.put(25, "lp user <username> permission set EconomyShopGUI.discounts.fivemedic true");
                    amyRewardCommands.put(50, "lp user <username> permission set EconomyShopGUI.discounts.tenmedic true");
                    amyRewardCommands.put(100, "lp user <username> permission set EconomyShopGUI.discounts.fifteenmedic true");
                    amyRewardCommands.put(200, "lp user <username> permission set EconomyShopGUI.discounts.twentymedic true");

                    // Define the reward thresholds and corresponding commands for Jake's Rapport Ticket
                    Map<Integer, String> jakeRewardCommands = new HashMap<>();
                    jakeRewardCommands.put(25, "lp user <username> permission set EconomyShopGUI.discounts.fiveclothing true");
                    jakeRewardCommands.put(50, "lp user <username> permission set EconomyShopGUI.discounts.tenclothing true");
                    jakeRewardCommands.put(100, "lp user <username> permission set EconomyShopGUI.discounts.fifteenclothing true");
                    jakeRewardCommands.put(200, "lp user <username> permission set EconomyShopGUI.discounts.twentyclothing true");

                    // Define the reward thresholds and corresponding commands for Matheo's Rapport Ticket
                    Map<Integer, String> matheoRewardCommands = new HashMap<>();
                    matheoRewardCommands.put(25, "lp user <username> permission set EconomyShopGUI.discounts.fiveenchanting true");
                    matheoRewardCommands.put(50, "lp user <username> permission set EconomyShopGUI.discounts.tenenchanting true");
                    matheoRewardCommands.put(100, "lp user <username> permission set EconomyShopGUI.discounts.fifteenenchanting true");
                    matheoRewardCommands.put(200, "lp user <username> permission set EconomyShopGUI.discounts.twentyenchanting true");

                    // Define the reward thresholds and corresponding commands for Dmitry's Rapport Ticket
                    Map<Integer, String> dmitryRewardCommands = new HashMap<>();
                    dmitryRewardCommands.put(25, "lp user <username> permission set EconomyShopGUI.discounts.fiveweapons true");
                    dmitryRewardCommands.put(50, "lp user <username> permission set EconomyShopGUI.discounts.tenweapons true");
                    dmitryRewardCommands.put(100, "lp user <username> permission set EconomyShopGUI.discounts.fifteenweapons true");
                    dmitryRewardCommands.put(200, "lp user <username> permission set EconomyShopGUI.discounts.twentyweapons true");

                    // Handle "Amy's Rapport Ticket"
                    if (displayName.equals(ChatColor.GREEN + "Amy's Rapport Ticket")) {
                        processTicket(player, "Amy's Rapport Ticket", amyRewardCommands);
                    }
                    // Handle "Jake's Rapport Ticket"
                    else if (displayName.equals(ChatColor.LIGHT_PURPLE + "Jake's Rapport Ticket")) {
                        processTicket(player, "Jake's Rapport Ticket", jakeRewardCommands);
                    }
                    // Handle "Matheo's Rapport Ticket"
                    else if (displayName.equals(ChatColor.AQUA + "Matheo's Rapport Ticket")) {
                        processTicket(player, "Matheo's Rapport Ticket", matheoRewardCommands);
                    }
                    // Handle "Dmitry's Rapport Ticket"
                    else if (displayName.equals(ChatColor.RED + "Dmitry's Rapport Ticket")) {
                        processTicket(player, "Dmitry's Rapport Ticket", dmitryRewardCommands);
                    } else {
                        getLogger().info("Player is holding an item, but it's not a recognized Rapport Ticket.");
                    }
                }
            } else {
                getLogger().info("Player is not holding a valid item.");
            }
        } else {
            getLogger().info("Interacted entity is not the target NPC.");
        }
    }

    private void processTicket(Player player, String ticketType, Map<Integer, String> rewardCommands) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        player.sendMessage(ChatColor.GREEN + "Rapport item returned.");
        // Deduct one item
        itemInHand.setAmount(itemInHand.getAmount() - 1);
        getLogger().info("One " + ticketType + " consumed.");


        // Track the player's interactions for this specific ticket type
        UUID playerId = player.getUniqueId();
        interactionCounts.putIfAbsent(ticketType, new HashMap<>());
        Map<UUID, Integer> playerCounts = interactionCounts.get(ticketType);
        int currentCount = playerCounts.getOrDefault(playerId, 0) + 1;
        playerCounts.put(playerId, currentCount);
        getLogger().info("Interaction count for player " + player.getName() + " with " + ticketType + ": " + currentCount);

        // Check against multiple thresholds and run the appropriate command
        if (currentCount <= 200) { // Only process up to 200 turn-ins
            for (Map.Entry<Integer, String> entry : rewardCommands.entrySet()) {
                if (currentCount == entry.getKey()) {
                    String command = entry.getValue().replace("<username>", player.getName());
                    CommandSender console = Bukkit.getServer().getConsoleSender();
                    Bukkit.dispatchCommand(console, command);
                    player.sendMessage("You have turned in " + entry.getKey() + " of " + ticketType + "! You have been granted a shop discount for that NPC.");
                    getLogger().info("Granted permission to " + player.getName() + " for turning in " + entry.getKey() + " " + ticketType);
                }
            }
        } else {
            player.sendMessage("§c§l(!) §cPlayer " + player.getName() + " has turned in over 200 " + ticketType + ". No further rewards granted.");
        }
    }

    private void saveInteractionCounts() {
        for (String ticketType : interactionCounts.keySet()) {
            for (UUID playerId : interactionCounts.get(ticketType).keySet()) {
                dataConfig.set(ticketType + "." + playerId.toString(), interactionCounts.get(ticketType).get(playerId));
            }
        }
        try {
            dataConfig.save(dataFile);
            getLogger().info("Interaction counts saved.");
        } catch (IOException e) {
            getLogger().severe("Could not save interaction counts: " + e.getMessage());
        }
    }

    private void loadInteractionCounts() {
        if (dataConfig == null) {
            getLogger().warning("dataConfig is null. Interaction counts cannot be loaded.");
            return;
        }

        for (String ticketType : dataConfig.getKeys(false)) {
            ConfigurationSection section = dataConfig.getConfigurationSection(ticketType);
            if (section != null) {
                Map<UUID, Integer> playerCounts = new HashMap<>();
                for (String key : section.getKeys(false)) {
                    UUID playerId = UUID.fromString(key);
                    int count = dataConfig.getInt(ticketType + "." + key);
                    playerCounts.put(playerId, count);
                }
                interactionCounts.put(ticketType, playerCounts);
            } else {
                getLogger().warning("Configuration section for " + ticketType + " is null. Skipping.");
            }
        }
        getLogger().info("Interaction counts loaded.");
    }

    private void loadDataFile() {
        dataFile = new File(getDataFolder(), "data.yml");

        // Create the file if it doesn't exist
        if (!dataFile.exists()) {
            try {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
                getLogger().info("data.yml file created successfully.");
            } catch (IOException e) {
                getLogger().severe("Could not create data.yml file: " + e.getMessage());
            }
        }

        // Load the file configuration
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    // Factory method for creating Rapport items
    private ItemStack createRapportItem(String name, ChatColor color) {
        // Create the item (e.g., Paper)
        ItemStack item = new ItemStack(Material.PAPER);  // You can change Material.PAPER to another item type if needed

        // Get the item meta to set the display name and lore
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            // Set the display name
            meta.setDisplayName(color + name + "'s Rapport Ticket");

            // Optionally, add lore
            meta.setLore(Collections.singletonList(ChatColor.GRAY + "A special rapport ticket."));

            // Apply the meta to the item
            item.setItemMeta(meta);
        }

        return item;
    }

    // Handle custom commands
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("rapport")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                UUID playerId = player.getUniqueId();
                player.sendMessage(ChatColor.GOLD + "Your Rapport Levels:");

                // Define the reward thresholds for each ticket type
                Map<String, Integer[]> rewardThresholds = new HashMap<>();
                rewardThresholds.put("Amy's Rapport Ticket", new Integer[]{25, 50, 100, 200});
                rewardThresholds.put("Jake's Rapport Ticket", new Integer[]{25, 50, 100, 200});
                rewardThresholds.put("Matheo's Rapport Ticket", new Integer[]{25, 50, 100, 200});
                rewardThresholds.put("Dmitry's Rapport Ticket", new Integer[]{25, 50, 100, 200});

                // Display the player's progress for each rapport item type
                for (String ticketType : rewardThresholds.keySet()) {
                    Map<UUID, Integer> playerCounts = interactionCounts.getOrDefault(ticketType, new HashMap<>());
                    int count = playerCounts.getOrDefault(playerId, 0);

                    // Determine the next level threshold
                    Integer[] thresholds = rewardThresholds.get(ticketType);
                    int nextThreshold = -1;
                    for (int threshold : thresholds) {
                        if (count < threshold) {
                            nextThreshold = threshold;
                            break;
                        }
                    }

                    if (nextThreshold == -1) {
                        player.sendMessage(ChatColor.YELLOW + ticketType + ": " + count + " items turned in. You have reached the maximum level.");
                    } else {
                        int itemsNeeded = nextThreshold - count;
                        player.sendMessage(ChatColor.YELLOW + ticketType + ": " + count + " items turned in. " + itemsNeeded + " more to reach the next level (" + nextThreshold + ").");
                    }
                }

                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "This command can only be run by a player.");
                return true;
            }
        }

        // Handle the /giveAmyRapportItem command
        if (command.getName().equalsIgnoreCase("giveAmyRapportItem")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be run by a player.");
                return true;
            }

            Player player = (Player) sender;

            // Ensure the command is only run by OPs
            if (!player.isOp()) {
                player.sendMessage(ChatColor.RED + "You do not have permission to run this command.");
                return true;
            }

            ItemStack amysRapportTicket = createRapportItem("Amy", ChatColor.GREEN);
            player.getInventory().addItem(amysRapportTicket);
            player.sendMessage(ChatColor.GOLD + "You have received Amy's Rapport Ticket!");
            return true;
        }

        // Handle the /giveJakeRapportItem command
        if (command.getName().equalsIgnoreCase("giveJakeRapportItem")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be run by a player.");
                return true;
            }

            Player player = (Player) sender;

            // Ensure the command is only run by OPs
            if (!player.isOp()) {
                player.sendMessage(ChatColor.RED + "You do not have permission to run this command.");
                return true;
            }

            ItemStack jakesRapportTicket = createRapportItem("Jake", ChatColor.LIGHT_PURPLE);
            player.getInventory().addItem(jakesRapportTicket);
            player.sendMessage(ChatColor.GOLD + "You have received Jake's Rapport Ticket!");
            return true;
        }

        // Handle the /giveMatheoRapportItem command
        if (command.getName().equalsIgnoreCase("giveMatheoRapportItem")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be run by a player.");
                return true;
            }

            Player player = (Player) sender;

            // Ensure the command is only run by OPs
            if (!player.isOp()) {
                player.sendMessage(ChatColor.RED + "You do not have permission to run this command.");
                return true;
            }

            ItemStack matheosRapportTicket = createRapportItem("Matheo", ChatColor.AQUA);
            player.getInventory().addItem(matheosRapportTicket);
            player.sendMessage(ChatColor.GOLD + "You have received Matheo's Rapport Ticket!");
            return true;
        }

        // Handle the /giveDmitryRapportItem command
        if (command.getName().equalsIgnoreCase("giveDmitryRapportItem")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be run by a player.");
                return true;
            }

            Player player = (Player) sender;

            // Ensure the command is only run by OPs
            if (!player.isOp()) {
                player.sendMessage(ChatColor.RED + "You do not have permission to run this command.");
                return true;
            }

            ItemStack dmitrysRapportTicket = createRapportItem("Dmitry", ChatColor.RED);
            player.getInventory().addItem(dmitrysRapportTicket);
            player.sendMessage(ChatColor.GOLD + "You have received Dmitry's Rapport Ticket!");
            return true;
        }

        return false;
    }
}
