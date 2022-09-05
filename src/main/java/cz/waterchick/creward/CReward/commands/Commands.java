package cz.waterchick.creward.CReward.commands;


import cz.waterchick.creward.CReward.GUI;
import cz.waterchick.creward.CReward.Main;
import cz.waterchick.creward.CReward.Reward;
import cz.waterchick.creward.CReward.managers.PlayerManager;
import cz.waterchick.creward.CReward.managers.RewardManager;
import cz.waterchick.creward.CReward.managers.configurations.PluginConfig;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

    private GUI gui;
    private PluginConfig pluginConfig;
    private PlayerManager playerManager;
    private RewardManager rewardManager;

    public Commands(GUI gui, PluginConfig pluginConfig, PlayerManager playerManager, RewardManager rewardManager){
        this.gui = gui;
        this.pluginConfig = pluginConfig;
        this.playerManager = playerManager;
        this.rewardManager = rewardManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(label.equalsIgnoreCase("creward") || label.equalsIgnoreCase("cr")){
            if(args.length == 0){
                if(sender instanceof Player){
                    Player p = (Player) sender;
                    gui.openGUI(p);
                }
            }if(args.length == 1){
                if(args[0].equalsIgnoreCase("reload")){
                    if (sender.hasPermission("cr.reload")) {
                        pluginConfig.reloadConfig();
                        rewardManager.loadRewards();
                        if(Main.getPlugin().isDisabled()){
                            sender.sendMessage(pluginConfig.getPrefix() + Main.Color("&cErrors occured, check console"));
                        }else {
                            sender.sendMessage(pluginConfig.getPrefix() + pluginConfig.getConfigReload());
                        }
                    }else{
                        Player p = (Player) sender;
                        sender.sendMessage(pluginConfig.getPrefix() + PlaceholderAPI.setPlaceholders(p,pluginConfig.getNoPerm()));
                        return false;
                    }
                }if(args[0].equalsIgnoreCase("help")){
                    sender.sendMessage(Main.Color("&8&m--------------------------------"));
                    sender.sendMessage(Main.Color("&2/cr | /creward &7- Shows the GUI"));
                    sender.sendMessage(Main.Color("&2/cr reload &7- Reloads the plugin"));
                    sender.sendMessage(Main.Color("&2/cr reset <reward> <player> &7- Reloads the player's reward time"));
                    sender.sendMessage(Main.Color("&2/cr help &7- Shows this message"));
                    sender.sendMessage(Main.Color("&8&m--------------------------------"));
                }
            }if(args.length == 3){
                if(args[0].equalsIgnoreCase("reset")){
                    if(sender.hasPermission("cr.reset")){
                        Reward reward = rewardManager.getReward(args[1]);
                        Player player = Bukkit.getPlayer(args[2]);
                        if(reward == null){
                            return false;
                        }
                        if(player == null){
                            return false;
                        }
                        playerManager.reset(reward,player.getUniqueId());
                        sender.sendMessage(pluginConfig.getPrefix() + PlaceholderAPI.setPlaceholders(player,pluginConfig.getReset()));
                    }else{
                        Player p = (Player) sender;
                        sender.sendMessage(pluginConfig.getPrefix() + PlaceholderAPI.setPlaceholders(p,pluginConfig.getNoPerm()));
                        return false;
                    }
                }
            }
        }
        return false;
    }
}
