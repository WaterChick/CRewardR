package cz.waterchick.creward.managers.gui;

import cz.waterchick.creward.CItem;
import cz.waterchick.creward.CReward;
import cz.waterchick.creward.dependencies.HDBAPI;
import cz.waterchick.creward.managers.PlayerManager;
import cz.waterchick.creward.managers.reward.Reward;
import cz.waterchick.creward.managers.reward.RewardManager;
import cz.waterchick.creward.managers.configurations.PluginConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import cz.waterchick.creward.dependencies.PlaceholderAPI;

import java.util.List;
import java.util.Map;

public class GUI {

    private static GUI instance;

    private final PluginConfig pluginConfig;
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

    public GUI(){
        instance = this;
        this.pluginConfig = PluginConfig.getInstance();
        this.rewardManager = RewardManager.getInstance();
        this.playerManager = PlayerManager.getInstance();

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

    public static GUI getInstance() {
        return instance;
    }

    public void openInventory(final Player p){
        String title = this.title;
        if(CReward.getPlugin().isPapiEnabled()){
            title = PlaceholderAPI.setPlaceholders(title,null,p);
        }
        Inventory inv = Bukkit.createInventory(null,rows*9, title);
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
                if(CReward.getPlugin().isPapiEnabled()){
                    title = PlaceholderAPI.setPlaceholders(title,null,p);
                }
                if(!title.equals(getTitle())){
                    cancel();
                    return;
                }
                if(isFillerEnabled()){
                    ItemStack fillerItemStack = new ItemStack(getFillerMaterial(), 1, (short) getFillerItemData());
                    for(Integer slot : getFillerSlots()){
                        inventory.setItem(slot,fillerItemStack);
                    }
                }
                for(Reward reward : rewardManager.getRewards()){
                    CItem cItem = reward.getCItem(playerManager.canClaim(reward,p.getUniqueId()));
                    ItemStack itemStack = cItem.getItemStack();
                    if(itemStack.getType().toString().equalsIgnoreCase("LEGACY_SKULL_ITEM") || itemStack.getType().toString().equalsIgnoreCase("SKULL_ITEM") || itemStack.getType().toString().equalsIgnoreCase("PLAYER_HEAD")){
                        if(cItem.getSkullData().equals("%player%") || cItem.getSkullData().equals("%player_name%")) {
                            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
                            skullMeta.setOwner(cItem.getSkullData().replace("%player_name%", p.getName()));
                            itemStack.setItemMeta(skullMeta);
                        }else{
                            itemStack = HDBAPI.getHead(cItem.getSkullData());
                        }
                    }
                    if(itemStack == null){
                        continue;
                    }
                    ItemMeta meta = itemStack.getItemMeta();
                    ItemMeta orgMeta = cItem.getItemStack().getItemMeta();
                    Integer slot = reward.getSlot();
                    if(orgMeta.hasDisplayName()) {
                        meta.setDisplayName(orgMeta.getDisplayName());
                    }
                    if(orgMeta.hasLore()) {
                        List<String> lore = orgMeta.getLore();
                        if(CReward.getPlugin().isPapiEnabled()){
                            lore = PlaceholderAPI.setPlaceholders(lore,reward,p);
                        }
                        meta.setLore(lore);
                    }
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    itemStack.setItemMeta(meta);
                    if(cItem.isGlow()) {
                        itemStack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
                    }
                    inventory.setItem(slot,itemStack);
                }
                for(Map.Entry<CItem,Integer> entry : pluginConfig.getOtherItems().entrySet()){
                    CItem cItem = entry.getKey();
                    Integer slot = entry.getValue();
                    ItemStack itemStack = cItem.getItemStack();

                    if(itemStack == null){
                        continue;
                    }
                    if(itemStack.getType().toString().equalsIgnoreCase("LEGACY_SKULL_ITEM") || itemStack.getType().toString().equalsIgnoreCase("SKULL_ITEM") || itemStack.getType().toString().equalsIgnoreCase("PLAYER_HEAD")){
                        if(cItem.getSkullData().equals("%player%") || cItem.getSkullData().equals("%player_name%")) {
                            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
                            skullMeta.setOwner(cItem.getSkullData().replace("%player_name%", p.getName()));
                            itemStack.setItemMeta(skullMeta);
                        }else{
                            itemStack = HDBAPI.getHead(cItem.getSkullData());
                        }
                    }
                    if(itemStack == null){
                        continue;
                    }
                    ItemMeta meta = itemStack.getItemMeta();
                    ItemMeta orgMeta = cItem.getItemStack().getItemMeta();
                    if(orgMeta.hasDisplayName()) {
                        meta.setDisplayName(orgMeta.getDisplayName());
                    }
                    if(orgMeta.hasLore()) {
                        List<String> lore = orgMeta.getLore();
                        if(CReward.getPlugin().isPapiEnabled()){
                             lore = PlaceholderAPI.setPlaceholders(lore,null,p);
                        }
                        meta.setLore(lore);
                    }
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    itemStack.setItemMeta(meta);
                    if(cItem.isGlow()) {
                        itemStack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
                    }
                    inventory.setItem(slot,itemStack);
                }
            }
        }.runTaskTimer(CReward.getPlugin(), 0, 20L);
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
