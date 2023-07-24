package cz.waterchick.creward.commands;


import cz.waterchick.creward.managers.gui.GUI;
import cz.waterchick.creward.CReward;
import cz.waterchick.creward.managers.reward.Reward;
import cz.waterchick.creward.Utilities;
import cz.waterchick.creward.dependencies.PlaceholderAPI;
import cz.waterchick.creward.managers.PlayerManager;
import cz.waterchick.creward.managers.reward.RewardManager;
import cz.waterchick.creward.managers.configurations.PluginConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Commands implements CommandExecutor {

    private GUI gui;
    private PluginConfig pluginConfig;
    private PlayerManager playerManager;
    private RewardManager rewardManager;

    public Commands(){
        this.gui = GUI.getInstance();
        this.pluginConfig = PluginConfig.getInstance();
        this.playerManager = PlayerManager.getInstance();
        this.rewardManager = RewardManager.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(label.equalsIgnoreCase("creward") || label.equalsIgnoreCase("cr")){
            if(args.length == 0){
                if(sender instanceof Player){
                    Player p = (Player) sender;
                    gui.openInventory(p);
                }
            }if(args.length == 1){
                if(args[0].equalsIgnoreCase("reload")){
                    if (sender.hasPermission("cr.reload")) {
                        pluginConfig.reloadConfig();
                        rewardManager.loadRewards();
                        if(CReward.getPlugin().isDisabled()){
                            sender.sendMessage(pluginConfig.getPrefix() + Utilities.Color("&cErrors occured, check console"));
                        }else {
                            sender.sendMessage(pluginConfig.getPrefix() + pluginConfig.getConfigReload());
                        }
                    }else{
                        Player p = (Player) sender;
                        String msg = pluginConfig.getReset();
                        if(CReward.getPlugin().isPapiEnabled()){
                            msg = PlaceholderAPI.setPlaceholders(msg,null,p);
                        }
                        sender.sendMessage(pluginConfig.getPrefix() + msg);
                        return false;
                    }
                }if(args[0].equalsIgnoreCase("help")){
                    sender.sendMessage(Utilities.Color("&8&m--------------------------------"));
                    sender.sendMessage(Utilities.Color("&2/cr | /creward &7- Shows the GUI"));
                    sender.sendMessage(Utilities.Color("&2/cr reload &7- Reloads the plugin"));
                    sender.sendMessage(Utilities.Color("&2/cr reset <reward | *> <player> &7- Reloads the player's reward time"));
                    sender.sendMessage(Utilities.Color("&2/cr help &7- Shows this message"));
                    sender.sendMessage(Utilities.Color("&8&m--------------------------------"));
                }
            }if(args.length == 3){
                if(args[0].equalsIgnoreCase("reset")){
                    if(sender.hasPermission("cr.reset")){
                        Reward reward = rewardManager.getReward(args[1]);
                        Player p = Bukkit.getPlayer(args[2]);
                        boolean resetAll = args[1].equalsIgnoreCase("*");
                        if(reward == null && !resetAll){
                            return false;
                        }
                        if(p == null){
                            return false;
                        }
                        if(resetAll){
                            playerManager.resetAll(p.getUniqueId());
                        }else {
                            playerManager.reset(reward, p.getUniqueId());
                        }
                        String msg = pluginConfig.getReset();
                        if(CReward.getPlugin().isPapiEnabled()){
                            msg = PlaceholderAPI.setPlaceholders(msg,null,p);
                        }
                        sender.sendMessage(pluginConfig.getPrefix() + msg);
                    }else{
                        Player p = (Player) sender;
                        String msg = pluginConfig.getNoPerm();
                        if(CReward.getPlugin().isPapiEnabled()){
                            msg = PlaceholderAPI.setPlaceholders(msg,null,p);
                        }
                        sender.sendMessage(pluginConfig.getPrefix() + msg);
                        return false;
                    }
                }
            }
        }
        return false;
    }
}
