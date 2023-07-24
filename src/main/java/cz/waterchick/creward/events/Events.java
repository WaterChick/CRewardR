package cz.waterchick.creward.events;

import cz.waterchick.creward.CReward;
import cz.waterchick.creward.managers.reward.Reward;
import cz.waterchick.creward.managers.PlayerManager;
import cz.waterchick.creward.managers.reward.RewardManager;
import cz.waterchick.creward.managers.configurations.PluginConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import cz.waterchick.creward.dependencies.PlaceholderAPI;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Events implements Listener {

    private PluginConfig pluginConfig;
    private PlayerManager playerManager;
    private RewardManager rewardManager;

    public Events(){
        this.pluginConfig = PluginConfig.getInstance();
        this.playerManager = PlayerManager.getInstance();
        this.rewardManager = RewardManager.getInstance();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();
        UUID uuid = p.getUniqueId();
        String title = pluginConfig.getGuiTitle();
        if(CReward.getPlugin().isPapiEnabled()){
            title = PlaceholderAPI.setPlaceholders(title,null,p);
        }
        if(p.getOpenInventory().getTitle().equals(title)){
            e.setCancelled(true);
            if(e.getCurrentItem() == null){
                return;
            }
            ItemStack item = e.getCurrentItem();
            if(item.getItemMeta()==null){
                return;
            }
            Reward reward = rewardManager.getReward(e.getSlot());
            if(reward == null){
                return;
            }
            playerManager.claim(reward,uuid);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        CReward.getPlugin().getServer().getScheduler().runTaskLater(CReward.getPlugin(), new Runnable() {
            @Override
            public void run() {
                if(playerManager.claimable(p.getUniqueId()) > 0) {
                    if (p.hasPermission(pluginConfig.getAutoPickupPerm())) {
                        if(pluginConfig.isAutoPickup()) {
                            int i = playerManager.claimAll(p.getUniqueId());
                            p.sendMessage(pluginConfig.getPrefix() + pluginConfig.getAutoClaim().replace("%rewards%", i + ""));
                            return;
                        }
                    }
                    if(pluginConfig.getNotify().equalsIgnoreCase("false") || pluginConfig.getNotify() == null){
                        return;
                    }
                    String msg = pluginConfig.getNotify();
                    if(CReward.getPlugin().isPapiEnabled()){
                        msg = PlaceholderAPI.setPlaceholders(msg,null,p);
                    }
                    p.sendMessage(pluginConfig.getPrefix() + msg);
                }
            }
        }, 20L * 3);

    }
}
