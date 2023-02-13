package dk.iskold.task;

import dk.iskold.Main;
import dk.iskold.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitScheduler;

public class UpdadteInv implements Runnable{
    private Player player;
    private Inventory Inv;
    private BukkitScheduler scheduler;

    String name = Chat.colored(Main.configYML.getString("GUI.name"));

    public UpdadteInv(Player player, Inventory inv, BukkitScheduler scheduler) {
        this.Inv = inv;
        this.player = player;
        this.scheduler = scheduler;
    }


    @Override
    public void run() {

        if (player.getInventory().getName().equals(name)) {
            player.openInventory(Inv);
        }
        scheduler.cancelTasks(Main.instance);

    }

    //BukkitScheduler scheduler = Bukkit.getScheduler();
    //scheduler.scheduleSyncRepeatingTask(Main.instance, new UpdadteInv(p, inv, scheduler), 0L, 40L);
}



