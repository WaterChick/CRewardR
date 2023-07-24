package cz.waterchick.creward.managers.reward;

import cz.waterchick.creward.managers.DBManager;
import cz.waterchick.creward.managers.configurations.PluginConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewardManager {

    private static RewardManager instance;

    public static RewardManager getInstance() {
        return instance;
    }

    private HashMap<String, Reward> rewardHashMap = new HashMap<>();

    public RewardManager(){
        instance = this;
        loadRewards();
    }

    public void loadRewards(){
        PluginConfig pluginConfig = PluginConfig.getInstance();
        DBManager dbManager = DBManager.getInstance();
        rewardHashMap.clear();
        FileConfiguration config = pluginConfig.getConfig();
        ConfigurationSection section = config.getConfigurationSection("GUI.Rewards");
        for(String key : section.getKeys(false)){
            Reward reward = new Reward(config.getConfigurationSection("GUI.Rewards."+key),key);
            rewardHashMap.put(key,reward);
            dbManager.createTable(reward);
        }
    }

    public Reward getReward(Integer slot){
        for(Map.Entry<String,Reward> entry : rewardHashMap.entrySet()){
            Reward reward = entry.getValue();

            if(slot != reward.getSlot()){
                continue;
            }
            return reward;
        }
        return null;
    }

    public Reward getReward(String name){
        for(Map.Entry<String,Reward> entry : rewardHashMap.entrySet()){
            if(!entry.getKey().equalsIgnoreCase(name)){
                continue;
            }
            return entry.getValue();
        }
        return null;
    }

    public List<Reward> getRewards(){
        List<Reward> rewards = new ArrayList<>();
        for(Map.Entry<String,Reward> entry : rewardHashMap.entrySet()){
            rewards.add(entry.getValue());
        }
        return rewards;
    }


}
