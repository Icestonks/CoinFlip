package dk.iskold.task;

import dk.iskold.Main;
import dk.iskold.utils.Chat;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class UpdadteInv implements Runnable{
    private Player player;
    private Inventory Inv;
    String name = Chat.colored(Main.configYML.getString("GUI.name"));
    public UpdadteInv(Player player, Inventory inv) {
        this.Inv = inv;
        this.player = player;
    }


    @Override
    public void run() {

        if (player.getInventory().getName().equals(name)) {
            player.openInventory(Inv);
        }
    }
}



