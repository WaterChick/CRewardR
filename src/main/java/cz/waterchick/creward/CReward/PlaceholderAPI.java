package cz.waterchick.creward.CReward;

import cz.waterchick.creward.CReward.enums.ErrorType;
import cz.waterchick.creward.CReward.enums.TimeFormat;
import cz.waterchick.creward.CReward.managers.PlayerManager;
import cz.waterchick.creward.CReward.managers.RewardManager;
import cz.waterchick.creward.CReward.managers.configurations.PluginConfig;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class PlaceholderAPI extends PlaceholderExpansion {

    private PlayerManager playerManager;
    private RewardManager rewardManager;
    private PluginConfig pluginConfig;

    public PlaceholderAPI(PlayerManager playerManager, RewardManager rewardManager,PluginConfig pluginConfig){
        this.playerManager = playerManager;
        this.rewardManager = rewardManager;
        this.pluginConfig = pluginConfig;
    }

    @Override
    public String getAuthor() {
        return "Water_Chick";
    }

    @Override
    public String getIdentifier() {
        return "creward";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        Reward reward = null;
        try {
            reward = rewardManager.getReward(params.split("_")[1]);
        }catch (ArrayIndexOutOfBoundsException e){

        }
        switch (params.split("_")[0]){
            case "amount":
                return String.valueOf(playerManager.getAmount(player.getUniqueId()));
            case "time":
                if(reward == null){
                    return "";
                }
                if(playerManager.canClaim(reward,player.getUniqueId()) == ErrorType.NOPERM){
                    return pluginConfig.getNoPermInGUI();
                }
                if(playerManager.canClaim(reward,player.getUniqueId()) == ErrorType.SUCC){
                    return pluginConfig.getReadyToClaim();
                }
                if(playerManager.canClaim(reward,player.getUniqueId()) == ErrorType.NOTIME){
                    return playerManager.getTime(reward,player.getUniqueId(), TimeFormat.TOTAL);
                }
            case "hours":
                if(reward == null){
                    return "";
                }
                if(playerManager.canClaim(reward,player.getUniqueId()) == ErrorType.NOPERM){
                    return pluginConfig.getNoPermInGUI();
                }
                if(playerManager.canClaim(reward,player.getUniqueId()) == ErrorType.SUCC){
                    return pluginConfig.getReadyToClaim();
                }
                if(playerManager.canClaim(reward,player.getUniqueId()) == ErrorType.NOTIME){
                    return playerManager.getTime(reward,player.getUniqueId(),TimeFormat.TOTAL);
                }
            case "seconds":
                if(reward == null){
                    return "";
                }
                if(playerManager.canClaim(reward,player.getUniqueId()) == ErrorType.NOPERM){
                    return pluginConfig.getNoPermInGUI();
                }
                if(playerManager.canClaim(reward,player.getUniqueId()) == ErrorType.SUCC){
                    return pluginConfig.getReadyToClaim();
                }
                if(playerManager.canClaim(reward,player.getUniqueId()) == ErrorType.NOTIME){
                    return playerManager.getTime(reward,player.getUniqueId(),TimeFormat.TOTAL);
                }
            case "minutes":
                if(reward == null){
                    return "";
                }
                if(playerManager.canClaim(reward,player.getUniqueId()) == ErrorType.NOPERM){
                    return pluginConfig.getNoPermInGUI();
                }
                if(playerManager.canClaim(reward,player.getUniqueId()) == ErrorType.SUCC){
                    return pluginConfig.getReadyToClaim();
                }
                if(playerManager.canClaim(reward,player.getUniqueId()) == ErrorType.NOTIME){
                    return playerManager.getTime(reward,player.getUniqueId(),TimeFormat.TOTAL);
                }

        }

        return null; // Placeholder is unknown by the Expansion
    }
}
