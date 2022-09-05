package cz.waterchick.creward.CReward.managers.configurations;

import cz.waterchick.creward.CReward.Main;
import cz.waterchick.creward.CReward.Reward;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class DataConfig {

    private FileConfiguration config;
    private File file;

    public DataConfig(){
        createConfig();
    }

    public void createConfig(){
        file = new File(Main.getPlugin().getDataFolder(),"db.yml");
        if(!file.exists()){
            config = YamlConfiguration.loadConfiguration(file);
            saveConfig();
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void saveConfig(){
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadConfig(){
        config = YamlConfiguration.loadConfiguration(file);
    }

    public int getIntTime(Reward reward,UUID uuid){
        Integer time = config.getInt(reward.getName() + ".timeLeft."+uuid.toString());
        if(time == null){
            return 0;
        }else{
            return time;
        }
    }

    public void setIntTime(Reward reward, UUID uuid){
        config.set(reward.getName() + ".timeLeft."+uuid,reward.getCooldown());
    }

    public void resetIntTime(Reward reward, UUID uuid){
        config.set(reward.getName() + ".timeLeft."+uuid,0);
    }

    public FileConfiguration getConfig(){
        return config;
    }
}
