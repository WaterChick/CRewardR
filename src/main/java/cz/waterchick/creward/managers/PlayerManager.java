package cz.waterchick.creward.managers;

import cz.waterchick.creward.enums.ErrorType;
import cz.waterchick.creward.CReward;
import cz.waterchick.creward.managers.reward.Reward;
import cz.waterchick.creward.enums.TimeFormat;
import cz.waterchick.creward.managers.configurations.DataConfig;
import cz.waterchick.creward.managers.configurations.PluginConfig;
import cz.waterchick.creward.managers.reward.RewardManager;
import cz.waterchick.creward.dependencies.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PlayerManager {

    private static PlayerManager instance;

    public PlayerManager(){
        instance = this;
    }

    public static PlayerManager getInstance() {
        return instance;
    }


    public ErrorType canClaim(Reward reward, UUID uuid){
        Integer timeLeft = 0;
        if(PluginConfig.getInstance().getEnable()){
            timeLeft = DBManager.getInstance().getTime(uuid,reward);
        }else{
            timeLeft = DataConfig.getInstance().getIntTime(reward,uuid);
        }if(reward.getPermission() != null && !reward.getPermission().equalsIgnoreCase( "")) {
            Player p = Bukkit.getPlayer(uuid);
            if(p == null){
                return ErrorType.NOPERM;
            }
            if (!p.hasPermission(reward.getPermission())) {
                return ErrorType.NOPERM;
            }
        }
        if(Bukkit.getPlayer(uuid) == null) {
            return ErrorType.NOTIME;
        }
        if(timeLeft == 0){
            return ErrorType.SUCC;
        }
        else{
            return ErrorType.NOTIME;
        }
    }

    public void claim(Reward reward, UUID uuid){
        PluginConfig pluginConfig = PluginConfig.getInstance();
        DBManager dbManager = DBManager.getInstance();
        DataConfig dataConfig = DataConfig.getInstance();
        String prefix = pluginConfig.getPrefix();
        Player p = Bukkit.getPlayer(uuid);
        if(p == null){
            return;
        }
        if(canClaim(reward,uuid) == ErrorType.SUCC){
            if(PluginConfig.getInstance().isCloseOnClaim()){
                p.closeInventory();
            }

            String msg = pluginConfig.getYesClaim();
            giveReward(reward,uuid);
            if(pluginConfig.getEnable()){
                dbManager.insertTable(uuid,reward.getCooldown(),reward);
            }else {
                dataConfig.setIntTime(reward, uuid);
            }
            if(CReward.getPlugin().isPapiEnabled()){
                msg = PlaceholderAPI.setPlaceholders(msg,reward,p);
            }
            p.sendMessage(prefix+msg);
            return;
        }
        if(canClaim(reward,uuid) == ErrorType.NOPERM){
            String msg = pluginConfig.getNoPerm();
            if(CReward.getPlugin().isPapiEnabled()){
                msg = PlaceholderAPI.setPlaceholders(msg,reward,p);
            }
            p.sendMessage(prefix+msg);
            return;
        }if(canClaim(reward,uuid) == ErrorType.NOTIME){
            String msg = pluginConfig.getNoClaim();
            if(CReward.getPlugin().isPapiEnabled()){
                msg = PlaceholderAPI.setPlaceholders(msg,reward,p);
            }
            p.sendMessage(prefix+msg);
        }
    }

    public void giveReward(Reward reward, UUID uuid){
        PluginConfig pluginConfig = PluginConfig.getInstance();
        Player p = Bukkit.getPlayer(uuid);
        p.playSound(p.getLocation(),pluginConfig.getPickupSound(), pluginConfig.getPickupVolume(), pluginConfig.getPickupPitch());
        List<String> commands = reward.getCommands();
        if(commands == null || commands.isEmpty()) {
            return;
        }
        for (String cmd : reward.getCommands()) {
            if(CReward.getPlugin().isPapiEnabled()){
                cmd = PlaceholderAPI.setPlaceholders(cmd,reward,p);
            }
            Bukkit.dispatchCommand(CReward.getPlugin().getServer().getConsoleSender(), cmd);
        }
    }

    public void reset(Reward reward, UUID uuid){
        PluginConfig pluginConfig = PluginConfig.getInstance();
        DBManager dbManager = DBManager.getInstance();
        DataConfig dataConfig = DataConfig.getInstance();
        if(pluginConfig.getEnable()){
            dbManager.insertTable(uuid,0,reward);
        }else {
            dataConfig.resetIntTime(reward, uuid);
        }
    }

    public void resetAll(UUID uuid){
        PluginConfig pluginConfig = PluginConfig.getInstance();
        DBManager dbManager = DBManager.getInstance();
        DataConfig dataConfig = DataConfig.getInstance();
        for(Reward reward : RewardManager.getInstance().getRewards()) {
            if (pluginConfig.getEnable()) {
                dbManager.insertTable(uuid, 0, reward);
            } else {
                dataConfig.resetIntTime(reward, uuid);
            }
        }
    }

    public int getAmount(UUID uuid){
        RewardManager rewardManager = RewardManager.getInstance();
        int amount = 0;
        for(Reward reward : rewardManager.getRewards()){
            if(canClaim(reward,uuid) == ErrorType.SUCC){
                amount++;
            }
        }
        return amount;
    }

    public String getTime(Reward reward, UUID uuid, TimeFormat timeFormat){
        PluginConfig pluginConfig = PluginConfig.getInstance();
        DBManager dbManager = DBManager.getInstance();
        DataConfig dataConfig = DataConfig.getInstance();
        int seconds;
        if(pluginConfig.getEnable()){
            seconds = dbManager.getTime(uuid,reward);
        }else {
            seconds = dataConfig.getIntTime(reward, uuid);
        }

        long sec = seconds % 60;
        long minutes = seconds % 3600 / 60;
        long hours = seconds % 86400 / 3600;
        long days = seconds / 86400;

        switch (timeFormat){
            case TIME:
                return String.format("%02dd %02dh %02dm %02ds",days, hours, minutes, sec);
            case HOURS:
                return String.format("%02dh", hours);
            case DAYS:
                return String.format("%02dd", days);
            case MINUTES:
                return String.format("%02dm", minutes);
            case SECONDS:
                return String.format("%02ds", sec);
        }
        return null;
    }

    public int claimAll(UUID uuid){
        RewardManager rewardManager = RewardManager.getInstance();
        int i = 0;
        for(Reward r : rewardManager.getRewards()){
            if(canClaim(r, uuid) == ErrorType.SUCC){
                claim(r,uuid);
                i++;
            }
        }
        return i;
    }
}
