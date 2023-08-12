package cz.waterchick.creward.dependencies;
import cz.waterchick.creward.CReward;
import cz.waterchick.creward.managers.ClassManager;
import cz.waterchick.creward.managers.reward.Reward;
import cz.waterchick.creward.enums.ErrorType;
import cz.waterchick.creward.enums.TimeFormat;
import cz.waterchick.creward.managers.PlayerManager;
import cz.waterchick.creward.managers.reward.RewardManager;
import cz.waterchick.creward.managers.configurations.PluginConfig;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlaceholderAPI extends PlaceholderExpansion {

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
        return "2.3.0";
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
        PluginConfig pluginConfig = PluginConfig.getInstance();
        PlayerManager playerManager = PlayerManager.getInstance();
        RewardManager rewardManager = RewardManager.getInstance();
        Reward reward = null;
        try {
            reward = rewardManager.getReward(params.split("_")[1]);
        }catch (ArrayIndexOutOfBoundsException e){

        }
        String identifier = params.split("_")[0];
        if(identifier.equalsIgnoreCase("amount")) return String.valueOf(playerManager.getAmount(player.getUniqueId()));

        if(reward == null){
            return "";
        }
        if(playerManager.canClaim(reward,player.getUniqueId()) == ErrorType.SUCC){
            return pluginConfig.getReadyToClaim();
        }
        if(playerManager.canClaim(reward,player.getUniqueId()) == ErrorType.NOTIME) {
            TimeFormat timeFormat = TimeFormat.getTimeFormat(identifier);
            if(timeFormat == null) {
                return null;
            }
            return playerManager.getTime(reward,player.getUniqueId(), timeFormat);
        }
        return null; // Placeholder is unknown by the Expansion
    }

    public static List<String> setPlaceholders(List<String> lore,Reward r,Player p){
        List<String> newlore = new ArrayList<>();
        for (String line : lore) {
            if (line.contains("%creward_time%")) {
                line = line.replace("%creward_time%", "%creward_time_" + r.getName() + "%");
            }
            if (line.contains("%creward_hours%")) {
                line = line.replace("%creward_hours%", "%creward_hours_" + r.getName() + "%");
            }
            if (line.contains("%creward_minutes%")) {
                line = line.replace("%creward_minutes%", "%creward_minutes_" + r.getName() + "%");
            }
            if (line.contains("%creward_seconds%")) {
                line = line.replace("%creward_seconds%", "%creward_seconds_" + r.getName() + "%");
            }
            if (CReward.getPlugin().isPapiEnabled()) {
                newlore.add(me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(p, line));
            }
        }
        return newlore;
    }

    public static String setPlaceholders(String msg ,Reward r,Player p){
            if (msg.contains("%creward_time%")) {
                msg = msg.replace("%creward_time%", "%creward_time_" + r.getName() + "%");
            }
            if (msg.contains("%creward_hours%")) {
                msg = msg.replace("%creward_hours%", "%creward_hours_" + r.getName() + "%");
            }
            if (msg.contains("%creward_minutes%")) {
                msg = msg.replace("%creward_minutes%", "%creward_minutes_" + r.getName() + "%");
            }
            if (msg.contains("%creward_seconds%")) {
                msg = msg.replace("%creward_seconds%", "%creward_seconds_" + r.getName() + "%");
            }
            if(CReward.getPlugin().isPapiEnabled()) {
                msg = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(p, msg);
            }
        return msg;
    }

}
