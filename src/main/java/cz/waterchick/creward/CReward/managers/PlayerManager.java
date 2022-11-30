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
import java.util.concurrent.TimeUnit;

public class PlayerManager {

    private DataConfig dataConfig;
    private PluginConfig pluginConfig;
    private RewardManager rewardManager;
    private DBManager dbManager;

    public PlayerManager(DataConfig dataConfig,PluginConfig pluginConfig,RewardManager rewardManager,DBManager dbManager){
        this.dataConfig = dataConfig;
        this.pluginConfig = pluginConfig;
        this.rewardManager = rewardManager;
        this.dbManager = dbManager;
    }



    public ErrorType canClaim(Reward reward, UUID uuid){
        Integer timeLeft = 0;
        if(pluginConfig.getEnable()){
            timeLeft = dbManager.getTime(uuid,reward);
        }else{
            timeLeft = dataConfig.getIntTime(reward,uuid);
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
        String prefix = pluginConfig.getPrefix();
        Player p = Bukkit.getPlayer(uuid);
        if(p == null){
            return;
        }
        if(canClaim(reward,uuid) == ErrorType.SUCC){
            if(pluginConfig.isCloseOnClaim()){
                p.closeInventory();
            }

            String msg = pluginConfig.getYesClaim();
            giveReward(reward,uuid);
            if(pluginConfig.getEnable()){
                dbManager.insertTable(uuid,reward.getCooldown(),reward);
            }else {
                dataConfig.setIntTime(reward, uuid);
            }
            Bukkit.getPlayer(uuid).sendMessage(prefix+Main.getPlugin().setPlaceholders(msg,reward,Bukkit.getPlayer(uuid)));
            return;
        }
        if(canClaim(reward,uuid) == ErrorType.NOPERM){
            String msg = pluginConfig.getNoPerm();
            Bukkit.getPlayer(uuid).sendMessage(prefix+Main.getPlugin().setPlaceholders(msg,reward,Bukkit.getPlayer(uuid)));
            return;
        }if(canClaim(reward,uuid) == ErrorType.NOTIME){
            String msg = pluginConfig.getNoClaim();
            Bukkit.getPlayer(uuid).sendMessage(prefix+Main.getPlugin().setPlaceholders(msg,reward,Bukkit.getPlayer(uuid)));
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
            Bukkit.dispatchCommand(Main.getPlugin().getServer().getConsoleSender(), Main.getPlugin().setPlaceholders(cmd,p));
        }
    }

    public void reset(Reward reward, UUID uuid){
        if(pluginConfig.getEnable()){
            dbManager.insertTable(uuid,0,reward);
        }else {
            dataConfig.resetIntTime(reward, uuid);
        }
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
            case TOTAL:
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
