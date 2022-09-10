package cz.waterchick.creward.CReward.managers;

import cz.waterchick.creward.CReward.enums.ErrorType;
import cz.waterchick.creward.CReward.Main;
import cz.waterchick.creward.CReward.Reward;
import cz.waterchick.creward.CReward.enums.TimeFormat;
import cz.waterchick.creward.CReward.managers.configurations.DataConfig;
import cz.waterchick.creward.CReward.managers.configurations.PluginConfig;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PlayerManager {

    private DataConfig dataConfig;
    private PluginConfig pluginConfig;
    private RewardManager rewardManager;

    public PlayerManager(DataConfig dataConfig,PluginConfig pluginConfig,RewardManager rewardManager){
        this.dataConfig = dataConfig;
        this.pluginConfig = pluginConfig;
        this.rewardManager = rewardManager;
    }



    public ErrorType canClaim(Reward reward, UUID uuid){
        if(dataConfig.getIntTime(reward,uuid) == 0){
            if(reward.getPermission() != null && !reward.getPermission().equalsIgnoreCase( "")) {
                if (Bukkit.getPlayer(uuid).hasPermission(reward.getPermission())) {
                    return ErrorType.SUCC;
                } else {
                    return ErrorType.NOPERM;
                }
            }else{
                return ErrorType.SUCC;
            }
        }
        else{
            return ErrorType.NOTIME;
        }
    }

    public void claim(Reward reward, UUID uuid){
        String prefix = pluginConfig.getPrefix();
        if(canClaim(reward,uuid) == ErrorType.SUCC){
            String msg = pluginConfig.getYesClaim();
            giveReward(reward,uuid);
            dataConfig.setIntTime(reward, uuid);
            Bukkit.getPlayer(uuid).sendMessage(prefix+Main.setPlaceholders(msg,reward,Bukkit.getPlayer(uuid)));
            return;
        }
        if(canClaim(reward,uuid) == ErrorType.NOPERM){
            String msg = pluginConfig.getNoPerm();
            Bukkit.getPlayer(uuid).sendMessage(prefix+Main.setPlaceholders(msg,reward,Bukkit.getPlayer(uuid)));
            return;
        }if(canClaim(reward,uuid) == ErrorType.NOTIME){
            String msg = pluginConfig.getNoClaim();
            Bukkit.getPlayer(uuid).sendMessage(prefix+Main.setPlaceholders(msg,reward,Bukkit.getPlayer(uuid)));
            return;
        }
    }

    public void giveReward(Reward reward, UUID uuid){
        Player p = Bukkit.getPlayer(uuid);
        p.playSound(p.getLocation(),pluginConfig.getPickupSound(), pluginConfig.getPickupVolume(), pluginConfig.getPickupPitch());
        List<String> commands = reward.getCommands();
        if(commands == null || commands.isEmpty()) {
            return;
        }
        for (String cmd : reward.getCommands()) {
            Bukkit.dispatchCommand(Main.getPlugin().getServer().getConsoleSender(), PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(uuid), cmd));
        }
    }

    public void reset(Reward reward, UUID uuid){
        dataConfig.resetIntTime(reward, uuid);
    }

    public int getAmount(UUID uuid){
        int amount = 0;
        for(Reward reward : rewardManager.getRewards()){
            if(canClaim(reward,uuid) == ErrorType.SUCC){
                amount++;
            }
        }
        return amount;
    }

    public String getTime(Reward reward, UUID uuid, TimeFormat timeFormat){
        int sec = dataConfig.getIntTime(reward,uuid);
        int hours = sec / 3600;
        int minutes = (sec % 3600) / 60;
        int seconds = sec % 60;
        switch (timeFormat){
            case TOTAL:
                return String.format("%02d:%02d:%02d", hours, minutes, seconds);
            case HOURS:
                return String.format("%02d", hours);
            case MINUTES:
                return String.format("%02d", minutes);
            case SECONDS:
                return String.format("%02d", seconds);
        }
        return null;
    }

    public int claimable(UUID uuid){
        int i = 0;
        for(Reward r : rewardManager.getRewards()){
            if(canClaim(r, uuid) == ErrorType.SUCC){
                i++;
            }
        }
        return i;
    }

    public int claimAll(UUID uuid){
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
