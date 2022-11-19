package cz.waterchick.creward.CReward;

import cz.waterchick.creward.CReward.enums.ErrorType;
import cz.waterchick.creward.CReward.managers.PlayerManager;
import cz.waterchick.creward.CReward.managers.RewardManager;
import cz.waterchick.creward.CReward.managers.configurations.PluginConfig;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GUI {

    private PluginConfig pluginConfig;
    private PlayerManager playerManager;
    private RewardManager rewardManager;

    public GUI(PluginConfig pluginConfig,PlayerManager playerManager,RewardManager rewardManager){
        this.pluginConfig = pluginConfig;
        this.playerManager = playerManager;
        this.rewardManager = rewardManager;
    }

    public void openGUI(Player p){
        Inventory inv = Bukkit.createInventory(null, pluginConfig.getGuiRows() * 9, Main.getPlugin().setPlaceholders(pluginConfig.getGuiTitle(),p));
        UUID uuid = p.getUniqueId();
        p.playSound(p.getLocation(),pluginConfig.getOpenSound(), pluginConfig.getOpenVolume(), pluginConfig.getOpenPitch());
        if(pluginConfig.isGuiFillerEnable()){
            if(!Main.Legacy()) {
                Material mat = pluginConfig.getGuiFillerItem();
                for (int slot : pluginConfig.getGuiFillerSlots()) {
                    inv.setItem(slot, new ItemStack(mat, 1));
                }
            }
            if(Main.Legacy()){
                Material mat = pluginConfig.getGuiFillerItem();
                int data = pluginConfig.getGuiFillerItemData();
                for (int slot : pluginConfig.getGuiFillerSlots()) {
                    try {
                        inv.setItem(slot, new ItemStack(mat, 1, (short) data));
                    }catch (ArrayIndexOutOfBoundsException e){
                        Main.getPlugin().getServer().getLogger().severe("Skipping slot "+ slot + " [ArrayIndexOutOfBoundsException]");
                        continue;
                    }
                }
            }
        }
        for(Reward r : rewardManager.getRewards()){
            int slot = getRewardSlot(r,uuid);
            ItemStack item = getRewardItem(r,uuid).clone();
            if(item.getType().toString().equalsIgnoreCase("LEGACY_SKULL_ITEM") || item.getType().toString().equalsIgnoreCase("SKULL_ITEM")){
                SkullMeta meta = (SkullMeta) item.getItemMeta();
                if(getSkullData(r, uuid) != null){
                    if(getSkullData(r,uuid).equalsIgnoreCase("%player%") || getSkullData(r,uuid).equalsIgnoreCase("%player_name%")){
                        meta.setOwner(p.getDisplayName());
                        List<String> newlore = Main.getPlugin().setPlaceholders(meta.getLore(),r,p);
                        meta.setLore(newlore);
                    }
                }
                item.setItemMeta(meta);
            }
            else {
                ItemMeta meta = item.getItemMeta();
                List<String> newlore = Main.getPlugin().setPlaceholders(meta.getLore(),r,p);
                meta.setLore(newlore);
                item.setItemMeta(meta);
            }
            inv.setItem(slot, item);
        }
        for(Map.Entry<ItemStack, Integer> entry : pluginConfig.getOtherItems().entrySet()){
            ItemStack item = entry.getKey();
            Integer slot = entry.getValue();
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(Main.Color(meta.getDisplayName()));
            List<String> newlore = new ArrayList<>();
            for(String line : meta.getLore()){
                line = Main.Color(line);
                line = Main.getPlugin().setPlaceholders(line,p);
                newlore.add(line);
            }
            meta.setLore(newlore);
            item.setItemMeta(meta);
            inv.setItem(slot,item);
        }
        p.openInventory(inv);
    }


    public ItemStack getRewardItem(Reward reward, UUID uuid){
        if(playerManager.canClaim(reward, uuid) == ErrorType.SUCC){
            return reward.getYesItem();
        }
        if(playerManager.canClaim(reward, uuid) == ErrorType.NOPERM){
            return reward.getNoPermItem();
        }
        else{
            return reward.getNoItem();
        }
    }

    public int getRewardSlot(Reward reward, UUID uuid) {
        if(playerManager.canClaim(reward, uuid) == ErrorType.SUCC){
            return reward.getYesSlot();
        }
        if(playerManager.canClaim(reward, uuid) == ErrorType.NOPERM){
            return reward.getNoPermSlot();
        }
        else{
            return reward.getNoSlot();
        }
    }

    public String getSkullData(Reward reward, UUID uuid){
        if(playerManager.canClaim(reward, uuid) == ErrorType.SUCC){
            return reward.getYesSkullData();
        }
        if(playerManager.canClaim(reward, uuid) == ErrorType.NOPERM){
            return reward.getNoPermSkullData();
        }
        else{
            return reward.getNoSkullData();
        }
    }
}
