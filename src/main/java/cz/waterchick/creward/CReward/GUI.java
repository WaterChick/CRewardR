package cz.waterchick.creward.CReward;

import cz.waterchick.creward.CReward.managers.PlayerManager;
import cz.waterchick.creward.CReward.managers.RewardManager;
import cz.waterchick.creward.CReward.managers.configurations.PluginConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class GUI {

    private final RewardManager rewardManager;
    private final PlayerManager playerManager;

    public final String title;
    private final Integer rows;

    private final Material fillerMaterial;
    private final int fillerItemData;
    private final List<Integer> fillerSlots;
    private final boolean fillerEnabled;

    private Sound openSound;
    private int openPitch;
    private int openVolume;

    public GUI(PluginConfig pluginConfig, RewardManager rewardManager, PlayerManager playerManager){
        this.rewardManager = rewardManager;
        this.playerManager = playerManager;

        this.title = pluginConfig.getGuiTitle();
        this.rows = pluginConfig.getGuiRows();

        this.fillerMaterial = pluginConfig.getGuiFillerItem();
        this.fillerSlots = pluginConfig.getGuiFillerSlots();
        this.fillerEnabled = pluginConfig.isGuiFillerEnable();
        this.fillerItemData = pluginConfig.getGuiFillerItemData();

        this.openSound = pluginConfig.getOpenSound();
        this.openPitch = pluginConfig.getOpenPitch();
        this.openVolume = pluginConfig.getOpenVolume();
    }

    public void openInventory(final Player p){
        Inventory inv = Bukkit.createInventory(null,rows*9, Main.getPlugin().setPlaceholders(title,p));
        p.openInventory(inv);

        p.playSound(p.getLocation(),openSound,openVolume,openPitch);

        updateInventory(p);
    }

    private void updateInventory(final Player p){
        new BukkitRunnable() {
            @Override
            public void run() {
                Inventory inventory = ((HumanEntity) p).getOpenInventory().getTopInventory();
                String title = p.getOpenInventory().getTitle();
                if(Main.getPlugin().isPapiEnabled()){
                    title = Main.getPlugin().setPlaceholders(title,p);
                }
                if(!title.equals(getTitle())){
                    cancel();
                    return;
                }
                if(isFillerEnabled()){
                    CItem item = new CItem(getFillerMaterial(),1,"",new ArrayList<>(),getFillerItemData());
                    for(Integer slot : getFillerSlots()){
                        inventory.setItem(slot,item.getItemStack());
                    }
                }
                for(Reward reward : rewardManager.getRewards()){
                    CItem cItem = reward.getItemStack(playerManager.canClaim(reward,p.getUniqueId()));
                    Integer slot = reward.getSlot(playerManager.canClaim(reward,p.getUniqueId()));
                    String skullData = reward.getSkullData(playerManager.canClaim(reward,p.getUniqueId()));
                    ItemStack itemStack = cItem.getItemStack();
                    ItemMeta meta = itemStack.getItemMeta();
                    meta.setLore(Main.getPlugin().setPlaceholders(meta.getLore(),reward,p));
                    itemStack.setItemMeta(meta);
                    if(itemStack.getType().toString().equalsIgnoreCase("LEGACY_SKULL_ITEM") || itemStack.getType().toString().equalsIgnoreCase("SKULL_ITEM")){
                        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
                        skullMeta.setOwner(skullData.replace("%player_name%",p.getName()));
                        itemStack.setItemMeta(skullMeta);
                    }
                    inventory.setItem(slot,itemStack);
                }
            }
        }.runTaskTimer(Main.getPlugin(), 0, 20L);
    }

    private String getTitle() {
        return title;
    }

    private Material getFillerMaterial() {
        return fillerMaterial;
    }

    private int getFillerItemData() {
        return fillerItemData;
    }

    private List<Integer> getFillerSlots() {
        return fillerSlots;
    }

    private boolean isFillerEnabled() {
        return fillerEnabled;
    }

}
