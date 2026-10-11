package ru.florestdev.taxes;

import org.bukkit.plugin.java.JavaPlugin;
import ru.florestdev.passportManager.ConfigManager;
import ru.florestdev.passportManager.PassportData;
import ru.florestdev.passportManager.PassportManager;

import java.util.ArrayList;
import java.util.List;

public final class Taxes extends JavaPlugin {

    private static PassportManager passportManager;

    public PassportManager getPassportManager() {
        return  passportManager;
    }

    public List<String> countries = new ArrayList<>();

    @Override
    public void onEnable() {
        getLogger().info("Starting...");
        getLogger().info("====================");
        getLogger().info("[Economy] Init...");
        EconomyManager.setupEconomy();
        getLogger().info("[Economy] INITIALIZED!");
        getLogger().info("====================");

        passportManager = (PassportManager) getServer().getPluginManager().getPlugin("PassportManager");
        getCommand("tax").setExecutor(new CommandActivity(this));
        getLogger().info("Initializing countries list...");
        for (List<PassportData> data : getPassportManager().getConfigManager().getDatabase().values()) {
            for (PassportData passportData : data) {
                String country = passportData.getCountry();
                if (!countries.contains(country)) {
                    countries.add(country);
                }
            }
        }
        getLogger().info("Ready to work!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling...");
    }
}
