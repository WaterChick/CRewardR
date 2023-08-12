package cz.waterchick.creward.managers;

import cz.waterchick.creward.CReward;
import cz.waterchick.creward.TabCompleter;
import cz.waterchick.creward.commands.Commands;
import cz.waterchick.creward.dependencies.HDBAPI;
import cz.waterchick.creward.dependencies.PlaceholderAPI;
import cz.waterchick.creward.events.Events;
import cz.waterchick.creward.managers.configurations.DataConfig;
import cz.waterchick.creward.managers.configurations.PluginConfig;
import cz.waterchick.creward.managers.gui.GUI;
import cz.waterchick.creward.managers.reward.RewardManager;
import org.bukkit.command.PluginCommand;

public class ClassManager {

    public ClassManager(){
        new PluginConfig();
        new DataConfig();
        new DBManager();
        new RewardManager();
        new PlayerManager();
        new GUI();
        PluginCommand cmd = CReward.getPlugin().getCommand("creward");
        cmd.setTabCompleter(new TabCompleter());
        cmd.setExecutor(new Commands());

        CReward.getPlugin().getServer().getPluginManager().registerEvents(new Events(), CReward.getPlugin());
        if(CReward.getPlugin().isPapiEnabled()) {
            new PlaceholderAPI().register();
        }
        if(CReward.getPlugin().isHDBEnabled()) {
            CReward.getPlugin().getServer().getPluginManager().registerEvents(new HDBAPI(), CReward.getPlugin());
        }
    }
}
