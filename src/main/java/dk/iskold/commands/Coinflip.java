package dk.iskold.commands;

import dk.iskold.Main;
import dk.iskold.task.UpdadteInv;
import dk.iskold.utils.Chat;
import dk.iskold.utils.Econ;
import dk.iskold.utils.Format;
import dk.iskold.utils.GUI;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class Coinflip implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        UUID uuid = ((Player) sender).getUniqueId();


        String name = Chat.colored(Main.configYML.getString("GUI.name"));
        String primary_color = Chat.colored(Main.configYML.getString("GUI.primary-color"));

        String help_head = Main.configYML.getString("GUI.help-head");
        String middle = Main.configYML.getString("GUI.middle");
        String arrow_left = Main.configYML.getString("GUI.arrow-left");
        String arrow_right = Main.configYML.getString("GUI.arrow-right");

        if (args.length == 0) {
            Inventory inv = Bukkit.createInventory(null, 9 * 6, name);

            int size = 0;
            int n = 0;
            inv.setItem(53, GUI.createItemStack(GUI.getSkull(help_head), primary_color + Chat.colored("&lOPRET COINFLIP"), "&7", "&8➥ " + primary_color + "&lOPRET COINFLIP", "&7", "&fDu kan lave dit eget", "&fcoinflip med commanden:", "&7", "&8● " + primary_color + "/coinflip opret <Antal>", "&7"));

            ConfigurationSection coinflipsSection = Main.coinflipsYML.getConfigurationSection("coinflips");
            if (coinflipsSection != null) {
                for (String key : Main.coinflipsYML.getConfigurationSection("coinflips").getKeys(true)) {
                    UUID p_uuid = UUID.fromString(key);
                    if (!Objects.equals(p_uuid, uuid)) {

                        OfflinePlayer player = Bukkit.getOfflinePlayer(p_uuid);
                        String playerName = player.getName();

                        ItemStack head = GUI.getPlayerSkull(playerName);
                        inv.setItem(n, GUI.createItemStack(head, Chat.colored(primary_color + "&lCOINFLIP " + (n + 1)), "&7", "&8● &fSpiller &8» " + primary_color + playerName, "&8● &fAntal &8» " + primary_color + Main.coinflipsYML.getInt("coinflips." + key)));
                        size++;
                        n++;

                    } else {
                        inv.setItem(45, GUI.createItemStack(GUI.getPlayerSkull(p.getName()), Chat.colored(primary_color + "&lDIT COINFLIP"), "&7", "&8● &fDit navn &8» " + primary_color + p.getName(), "&8● &fAntal &8» " + primary_color + Main.coinflipsYML.getInt("coinflips." + key), "&7", "&fTryk for at", "&cslette&f dit coinflip!", "&7", "&7&o((Venstreklik))"));
                    }
                }
            }

            inv.setItem(49, GUI.createItemStack(GUI.getSkull(middle), Chat.colored("&f&lSide"), "&fSide: &70/" + Math.round(size / (9 * 6))));

            BukkitScheduler scheduler = Bukkit.getScheduler();
            scheduler.scheduleSyncRepeatingTask(yourPlugin, new UpdateInvTask(player), 0L, 40L);
            p.openInventory(inv);
            return true;
        }


        if (args[0].equalsIgnoreCase("opret") || args[0].equalsIgnoreCase("add")) {


            //HVIS ARG ER STØRRE END 2
            if (!(args.length >= 2)) {
                sender.sendMessage(Chat.colored(Main.config.getConfig().getString("Messages.regexMatcherikke")));
                return true;
            }

            //TJEKKER OM DET ER ET TAL
            if (!(Pattern.matches("^[0-9]+$", args[1]))) {
                sender.sendMessage(Chat.colored(Main.config.getConfig().getString("Messages.regexMatcherikke")));
                return true;
            }

            //HVIS PERSONEN HAR ET COINFLIP
            if (Main.coinflips.getConfig().contains("coinflips." + uuid)) {
                List<String> messages = Main.config.getConfig().getStringList("Messages.Etigang");
                for (String message : messages) {
                    sender.sendMessage(Chat.colored(message));
                }
                return true;
            }

            int penge = Integer.parseInt(args[1]);
            if (Econ.getbalance(p.getName()) >= penge) {
                if (penge > 0) {
                    Main.coinflips.getConfig().set("coinflips." + uuid, penge);
                    Main.coinflips.saveConfig();
                    sender.sendMessage(Chat.colored(Main.config.getConfig().getString("Messages.Oprettet").replaceAll("%num%", Format.formatNum(penge))));
                    Econ.removeMoney(p, penge);
                    return true;

                } else {
                    sender.sendMessage(Chat.colored(Main.config.getConfig().getString("Messages.Over0")));
                    return true;
                }
            } else {
                sender.sendMessage(Chat.colored(Main.config.getConfig().getString("Messages.ikkenokpenge")));
                return true;
            }


        }
        if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("fjern")) {
            if (Main.coinflips.getConfig().contains("coinflips." + uuid)) {
                Econ.addMoney(p, Double.parseDouble(String.valueOf(Main.coinflips.getConfig().getDouble("coinflips." + uuid))));
                sender.sendMessage(Chat.colored(Main.config.getConfig().getString("Messages.DuFjernedCoinflip")));
                Main.coinflips.getConfig().set("coinflips." + uuid, null);
                Main.coinflips.saveConfig();
                return true;
            }
            sender.sendMessage(Chat.colored(Main.config.getConfig().getString("Messages.ikkenogenigang")));
            return true;
        }

        //Reload Command
        if (args[0].equalsIgnoreCase("reload") && p.hasPermission(Main.configYML.getString("Reload.permission"))) {
            boolean reloadSuccess;
            try {
                Main.config.reloadConfig();
                Main.configYML = Main.config.getConfig();


                reloadSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
                reloadSuccess = false;
            }
            if (reloadSuccess) {
                sender.sendMessage(Chat.colored("&aReload successfully completed"));
            } else {
                sender.sendMessage(Chat.colored("&cAn error occurred. Please check the console."));
            }
            return true;
        } else {
            return false;
        }
    }
}