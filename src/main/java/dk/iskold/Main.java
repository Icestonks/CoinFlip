package dk.iskold;

import dk.iskold.commands.Coinflip;
import dk.iskold.commands.TabComplete;
import dk.iskold.events.InventoryListener;
import dk.iskold.utils.Config;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Main extends JavaPlugin {

    public static Main instance;
    public static Config coinflips, license, config;
    public static FileConfiguration coinflipsYML, licenseYML, configYML;
    public static Economy econ = null;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        //license yml
        if (!(new File(getDataFolder(), "license.yml")).exists())
            saveResource("license.yml", false);

        license = new Config(this, null, "license.yml");
        licenseYML = license.getConfig();

        String license = licenseYML.getString("License");
        if(!new AdvancedLicense(license, "https://license.cutekat.dk/verify.php", this).debug().register()) return;


        //coinflips yml
        if (!(new File(getDataFolder(), "coinflips.yml")).exists())
            saveResource("coinflips.yml", false);

        coinflips = new Config(this, null, "coinflips.yml");
        coinflipsYML = coinflips.getConfig();

        //config yml
        if (!(new File(getDataFolder(), "config.yml")).exists())
            saveResource("config.yml", false);

        config = new Config(this, null, "config.yml");
        configYML = config.getConfig();

        getCommand("coinflip").setExecutor(new Coinflip());
        getCommand("coinflip").setTabCompleter(new TabComplete());
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        //VAULT // ECON
        if (!setupEconomy() ) {
            Bukkit.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            Bukkit.getLogger().severe(String.format(String.valueOf(getServer().getPluginManager().getPlugin("Vault"))));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupEconomy();

    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Main.coinflips.saveConfig();
    }


    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

}
