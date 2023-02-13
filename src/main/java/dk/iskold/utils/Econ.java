package dk.iskold.utils;

import dk.iskold.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class Econ {

    public static boolean addMoney(OfflinePlayer player, double amount) {

        return Main.econ.depositPlayer(player, amount).transactionSuccess();
    }

    public static boolean removeMoney(OfflinePlayer player, double amount) {
        return Main.econ.withdrawPlayer(player, amount).transactionSuccess();
    }

    private boolean addMoneyToPlayer(String playerName, double amount) {
        return Main.econ.depositPlayer(playerName, amount).transactionSuccess();
    }

    private static boolean createPlayerAccount(String playerName) {
        return Main.econ.createPlayerAccount(playerName);
    }

    public static double getbalance(String playerName) {
        return Main.econ.getBalance(playerName);
    }


}
