package cz.waterchick.creward.CReward.managers;

import cz.waterchick.creward.CReward.*;
import cz.waterchick.creward.CReward.commands.Commands;
import cz.waterchick.creward.CReward.events.Events;
import cz.waterchick.creward.CReward.managers.configurations.DataConfig;
import cz.waterchick.creward.CReward.managers.configurations.PluginConfig;
import org.bukkit.Bukkit;

public class ClassManager {

    private PluginConfig pluginConfig;
    private DataConfig dataConfig;
    private RewardManager rewardManager;
    private GUI gui;
    private PlayerManager playerManager;
    private PlaceholderAPI papi;

    public ClassManager(){
        pluginConfig = new PluginConfig();
        dataConfig = new DataConfig();
        rewardManager = new RewardManager(pluginConfig);
        if(!Main.getPlugin().isDisabled()) {
            playerManager = new PlayerManager(dataConfig, pluginConfig, rewardManager);
            gui = new GUI(pluginConfig, playerManager, rewardManager);
            papi = new PlaceholderAPI(playerManager, rewardManager, pluginConfig);
            Main.getPlugin().getCommand("creward").setExecutor(new Commands(gui, pluginConfig, playerManager, rewardManager));
            Main.getPlugin().getServer().getPluginManager().registerEvents(new Events(pluginConfig, playerManager, gui, rewardManager), Main.getPlugin());
            Main.getPlugin().getCommand("creward").setTabCompleter(new TabCompleter(rewardManager));
            papi.register();
        }
    }

    public RewardManager getRewardManager() {
        return rewardManager;
    }

    public DataConfig getDataConfig() {
        return dataConfig;
    }

    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public GUI getGui() {
        return gui;
    }
}
