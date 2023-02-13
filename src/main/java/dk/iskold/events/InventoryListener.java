package dk.iskold.events;

import dk.iskold.Main;
import dk.iskold.utils.Chat;
import dk.iskold.utils.Econ;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class InventoryListener implements Listener {
    Main plugin;

    public InventoryListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        String name = Chat.colored(Main.configYML.getString("GUI.name"));
        String primary_color = Chat.colored(Main.configYML.getString("GUI.primary-color"));
        Player player = (Player) e.getWhoClicked();

        if(e.getClickedInventory().getName().equals(name)) {
            e.setCancelled(true);

            if(e.getSlot() >= 45 && e.getSlot() <= 53) {
                if(e.getSlot() == 45 && e.getCurrentItem().getType() != Material.AIR) {
                    if (Main.coinflips.getConfig().contains("coinflips." + e.getWhoClicked().getUniqueId())) {
                        Econ.addMoney(e.getWhoClicked().getName(), Double.parseDouble(String.valueOf(Main.coinflips.getConfig().getDouble("coinflips." + e.getWhoClicked().getUniqueId()))));
                        Main.coinflips.getConfig().set("coinflips." + e.getWhoClicked().getUniqueId(), null);
                        Main.coinflips.saveConfig();
                        e.getClickedInventory().clear(e.getSlot());
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 5 , 1);

                    } else {
                        e.getClickedInventory().clear(e.getSlot());
                        e.getWhoClicked().sendMessage(Chat.colored(Main.config.getConfig().getString("Messages.flipertaget")));
                    }

                }
                return;
            }

            ItemStack item = e.getCurrentItem();
            if (item != null) {
                ItemMeta meta = item.getItemMeta();
                List<String> lore = meta.getLore();

                String[] opponent_split = lore.get(1).split(primary_color);
                OfflinePlayer opponent = Bukkit.getPlayer(opponent_split[1]);
                UUID opponent_uuid = opponent.getUniqueId();

                String[] amount_split = lore.get(2).split(primary_color);
                int amount = Integer.parseInt(amount_split[1]);
                if (Main.coinflips.getConfig().contains("coinflips." + opponent_uuid)) {
                    if (Econ.getbalance(e.getWhoClicked().getName()) >= amount) {
                        Econ.removeMoney(e.getWhoClicked().getName(), amount);
                        initialiseCoinflip(e.getWhoClicked().getUniqueId(), opponent_uuid);
                        e.getWhoClicked().closeInventory();
                    } else {
                        e.getWhoClicked().sendMessage(Chat.colored(Main.config.getConfig().getString("Messages.ikkenokpenge")));

                    }
                } else {
                    e.getClickedInventory().clear(e.getSlot());
                    e.getWhoClicked().sendMessage(Chat.colored(Main.config.getConfig().getString("Messages.flipertaget")));
                }

            }
        }

    }

    public void initialiseCoinflip(UUID player, UUID opponent) {
        Player player1 = Bukkit.getPlayer(player);
        Player opponent1 = Bukkit.getPlayer(opponent);
        String message = Chat.colored(Main.config.getConfig().getString("Messages.VinderenBliverFundet"));

        int player_money = Main.coinflips.getConfig().getInt("coinflips."+opponent);

        Main.coinflips.getConfig().set("coinflips." + opponent, null);
        Main.coinflips.saveConfig();
        opponent1.playSound(opponent1.getLocation(), Sound.NOTE_PLING, 5 , 1);
        player1.playSound(opponent1.getLocation(), Sound.NOTE_PLING, 5 , 1);

        for (int i = 3; i > 0; i--) {
            final int timeLeft = i;
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                player1.sendMessage(message.replace("%tid%", String.valueOf(timeLeft)));
                opponent1.sendMessage(message.replace("%tid%", String.valueOf(timeLeft)));
            }, (3 - i) * 20L);
        }



        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Random rand = new Random();
            List<Object> elements = Arrays.asList(opponent1, player1);
            int randomIndex = rand.nextInt(elements.size());
            Player randomPlayer = (Player) elements.get(randomIndex);

            Player loser;
            if (randomPlayer.equals(opponent1)) {
                loser = player1;
            } else {
                loser = opponent1;
            }



            randomPlayer.sendMessage(Chat.colored(Main.config.getConfig().getString("Messages.Duvandt").replace("%random%", loser.getName()).replace("%pris%", String.valueOf(player_money))));
            loser.sendMessage(Chat.colored(Main.config.getConfig().getString("Messages.DuTabte").replace("%vinder%", randomPlayer.getName()).replace("%pris%", String.valueOf(player_money))));
            Econ.addMoney(randomPlayer.getName(), player_money * 2);


        }, 60L);


    }

}
