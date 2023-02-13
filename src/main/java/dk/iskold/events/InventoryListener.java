package dk.iskold.events;

import com.mojang.authlib.GameProfile;
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

import java.util.*;

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
                        Econ.addMoney((OfflinePlayer) e.getWhoClicked(), Double.parseDouble(String.valueOf(Main.coinflips.getConfig().getDouble("coinflips." + e.getWhoClicked().getUniqueId()))));
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
                if(item.getType() == Material.AIR) return;

                ItemMeta meta = item.getItemMeta();
                List<String> lore = meta.getLore();

                String[] opponent_split = lore.get(1).split(primary_color);
                System.out.println(opponent_split[1]);
                UUID opponent_uuid = getPlayerUUID(opponent_split[1]);

                String[] amount_split = lore.get(2).split(primary_color);
                int amount = Integer.parseInt(amount_split[1]);
                if (Main.coinflips.getConfig().contains("coinflips." + opponent_uuid)) {
                    if (Econ.getbalance(e.getWhoClicked().getName()) >= amount) {
                        Econ.removeMoney((OfflinePlayer) e.getWhoClicked(), amount);
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
        String message = Chat.colored(Main.config.getConfig().getString("Messages.VinderenBliverFundet"));

        OfflinePlayer offline_player1 = Bukkit.getOfflinePlayer(player);
        String player1_name  = offline_player1.getName();
        Player player1 = Bukkit.getPlayer(player);

        OfflinePlayer offline_opponent1 = Bukkit.getOfflinePlayer(opponent);
        String opponent1_name  = offline_opponent1.getName();
        Player opponent1 = Bukkit.getPlayer(opponent);

        int player_money = Main.coinflips.getConfig().getInt("coinflips."+opponent);

        Main.coinflips.getConfig().set("coinflips." + opponent, null);
        Main.coinflips.saveConfig();

        if (player1 != null && player1.isOnline()) {
            player1.playSound(player1.getLocation(), Sound.NOTE_PLING, 5, 1);
        }
        if (opponent1 != null && opponent1.isOnline()) {
            opponent1.playSound(opponent1.getLocation(), Sound.NOTE_PLING, 5, 1);
        }

        for (int i = 3; i > 0; i--) {
            final int timeLeft = i;
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                if (opponent1 != null && opponent1.isOnline()) {
                    opponent1.sendMessage(message.replace("%tid%", String.valueOf(timeLeft)));
                }
                if (player1 != null && player1.isOnline()) {
                    player1.sendMessage(message.replace("%tid%", String.valueOf(timeLeft)));
                }
            }, (3 - i) * 20L);
        }



        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            List<UUID> list = Arrays.asList(player, opponent);
            Random randomizer = new Random();
            UUID random = list.get(randomizer.nextInt(list.size()));

            if(random.equals(player)) {
                if(player1 != null && player1.isOnline()) {
                    player1.sendMessage(Chat.colored(Main.config.getConfig().getString("Messages.Duvandt").replace("%random%", opponent1_name).replace("%pris%", String.valueOf(player_money))));
                }
                if (opponent1 != null && opponent1.isOnline()) {
                    opponent1.sendMessage(Chat.colored(Main.config.getConfig().getString("Messages.DuTabte").replace("%vinder%", player1_name).replace("%pris%", String.valueOf(player_money))));
                }

                Econ.addMoney(offline_player1, player_money*2);
            } else {
                if(opponent1 != null && opponent1.isOnline()) {
                    opponent1.sendMessage(Chat.colored(Main.config.getConfig().getString("Messages.Duvandt").replace("%random%", player1_name).replace("%pris%", String.valueOf(player_money))));
                }
                if(player1 != null && player1.isOnline()) {
                    player1.sendMessage(Chat.colored(Main.config.getConfig().getString("Messages.DuTabte").replace("%vinder%", opponent1_name).replace("%pris%", String.valueOf(player_money))));
                }

                Econ.addMoney(offline_opponent1, player_money * 2);
            }

        }, 60L);


    }

    public Map<String, UUID> uuidCache = new HashMap<>();

    public UUID getPlayerUUID(String playerName) {
        UUID playerUUID = uuidCache.get(playerName);
        if (playerUUID == null) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
            System.out.println(offlinePlayer);
            if (offlinePlayer.hasPlayedBefore()) {
                playerUUID = offlinePlayer.getUniqueId();
                uuidCache.put(playerName, playerUUID);
            }
        }
        return playerUUID;
    }
}
