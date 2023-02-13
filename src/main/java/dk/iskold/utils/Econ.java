package dk.iskold.utils;

import dk.iskold.Main;

public class Econ {

    public static boolean addMoney(String player, double amount) {

        return Main.econ.depositPlayer(player, amount).transactionSuccess();
    }

    public static boolean removeMoney(String player, double amount) {
        return Main.econ.bankWithdraw(player, amount).transactionSuccess();
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
