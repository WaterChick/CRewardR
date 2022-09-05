package cz.waterchick.creward.CReward.managers;

import cz.waterchick.creward.CReward.Reward;
import cz.waterchick.creward.CReward.managers.configurations.PluginConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewardManager {

    private PluginConfig pluginConfig;

    private HashMap<String, Reward> rewardHashMap = new HashMap<>();

    public RewardManager(PluginConfig pluginConfig){
        this.pluginConfig = pluginConfig;
        loadRewards();
    }

    public void loadRewards(){
        rewardHashMap.clear();
        FileConfiguration config = pluginConfig.getConfig();
        ConfigurationSection section = config.getConfigurationSection("GUI.Rewards");
        for(String key : section.getKeys(false)){
            rewardHashMap.put(key,new Reward(config.getConfigurationSection("GUI.Rewards."+key),key));
        }
    }

    public Reward getReward(Integer slot){
        for(Map.Entry<String,Reward> entry : rewardHashMap.entrySet()){
            Reward reward = entry.getValue();

            if(slot == reward.getYesSlot()){
                return reward;
            }
            if(slot == reward.getNoSlot()){
                return reward;
            }
            if(slot == reward.getNoPermSlot()){
                return reward;
            }
            continue;
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
