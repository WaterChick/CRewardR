package cz.waterchick.creward.CReward.managers.configurations;

import cz.waterchick.creward.CReward.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PluginConfig {

    private FileConfiguration config;
    private File file;

    private String prefix;
    private String yesClaim;
    private String noClaim;
    private String configReload;
    private String reset;
    private String noPerm;
    private String readyToClaim;
    private String notify;
    private String noPermInGUI;
    private String autoClaim;
    private boolean autoPickup;
    private String autoPickupPerm;

    private Sound openSound;
    private int openVolume;
    private int openPitch;

    private Sound pickupSound;
    private int pickupVolume;
    private int pickupPitch;

    private String guiTitle;
    private int guiRows;

    private boolean guiFillerEnable;
    private List<Integer> guiFillerSlots;
    private Material guiFillerItem;
    private int guiFillerItemData;

    private HashMap<ItemStack, Integer> otherItems = new HashMap<>();


    public PluginConfig() {
        createConfig();
    }

    public void createConfig() {
        file = new File(Main.getPlugin().getDataFolder(), "config.yml");
        if (!file.exists()) {
            config = YamlConfiguration.loadConfiguration(file);
            setDefaults();
            saveConfig();
        }
        config = YamlConfiguration.loadConfiguration(file);
        checkMessages();
        loadVars();
    }

    public void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(file);
        loadVars();
    }

    public void checkMessages() {
        if (!config.contains("Messages.noPermInGUI")) {
            config.set("Messages.noPermInGUI", "&cNo permission");
        }
        if (!config.contains("Messages.autoPickup")) {
            config.set("Messages.autoPickup", "&7Auto-Claimed &a%rewards% &7reward(s) (Premium)");
        }
        if (!config.contains("Autopickup")) {
            config.set("Autopickup.enabled", true);
            config.set("Autopickup.permission", "creward.autoclaim");
        }
        for(String key : config.getConfigurationSection("GUI.Rewards").getKeys(false)){
            if(!config.contains("GUI.Rewards."+key+".noPermClaim")){
                config.set("GUI.Rewards."+key+".Item.noPermClaim.Item.Material", "BARRIER");
                config.set("GUI.Rewards."+key+".Item.noPermClaim.Item.Data", 0);
                config.set("GUI.Rewards."+key+".Item.noPermClaim.Item.skullData", "%player_name%");
                config.set("GUI.Rewards."+key+".Item.noPermClaim.Item.amount", 1);
                config.set("GUI.Rewards."+key+".Item.noPermClaim.Title", "&cDaily reward");
                config.set("GUI.Rewards."+key+".Item.noPermClaim.Lore", "\n&cNo permission");
                config.set("GUI.Rewards."+key+".Item.noPermClaim.Slot", 10);
            }
        }
        if(!config.contains("Sounds")){
            if(Main.Legacy()){
                config.set("Sounds.OPEN_MENU.Sound", "CHEST_OPEN");
                config.set("Sounds.OPEN_MENU.Volume", 1);
                config.set("Sounds.OPEN_MENU.Pitch", 4);

                config.set("Sounds.REWARD_PICKUP.Sound", "CHICKEN_EGG_POP");
                config.set("Sounds.REWARD_PICKUP.Volume", 1);
                config.set("Sounds.REWARD_PICKUP.Pitch", 4);
            }
            if(!Main.Legacy()){
                config.set("Sounds.OPEN_MENU.Sound", "BLOCK_CHEST_OPEN");
                config.set("Sounds.OPEN_MENU.Volume", 1);
                config.set("Sounds.OPEN_MENU.Pitch", 4);

                config.set("Sounds.REWARD_PICKUP.Sound", "ENTITY_CHICKEN_EGG");
                config.set("Sounds.REWARD_PICKUP.Volume", 1);
                config.set("Sounds.REWARD_PICKUP.Pitch", 4);
            }
        }
        saveConfig();
    }

    public void setDefaults() {
        if (Main.Legacy()) {
            config.set("Messages.Prefix", "&8[&2CReward&8] &r");
            config.set("Messages.claim", "&aReward claimed!");
            config.set("Messages.noClaim", "&cYou can not claim this reward yet! &7(%creward_time%)");
            config.set("Messages.configReload", "&7Config reloaded");
            config.set("Messages.reset", "&7%player_name%'s reward time has been reseted");
            config.set("Messages.noPerm", "&cYou do not have permission to execute this command");
            config.set("Messages.readyToClaim", "&aReady to claim!");
            config.set("Messages.notify", "&7You can claim &a%creward_amount% &7reward(s)!");
            config.set("Messages.autoPickup", "&7Auto-Claimed &a%rewards% &7reward(s) (Premium)");
            config.set("Autopickup.enabled", true);
            config.set("Autopickup.permission", "creward.autoclaim");

            config.set("GUI.title", "&8Daily reward");
            config.set("GUI.rows", 5);

            config.set("GUI.Filler.Enable", true);
            config.set("GUI.Filler.Slots", new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4)));
            config.set("GUI.Filler.Item.Material", "STAINED_GLASS_PANE");
            config.set("GUI.Filler.Item.Data", 7);

            config.set("Sounds.OPEN_MENU.Sound", "CHEST_OPEN");
            config.set("Sounds.OPEN_MENU.Volume", 1);
            config.set("Sounds.OPEN_MENU.Pitch", 4);

            config.set("Sounds.REWARD_PICKUP.Sound", "CHICKEN_EGG_POP");
            config.set("Sounds.REWARD_PICKUP.Volume", 1);
            config.set("Sounds.REWARD_PICKUP.Pitch", 4);

            config.set("GUI.Rewards.test.Item.yesClaim.Item.Material", "STORAGE_MINECART");
            config.set("GUI.Rewards.test.Item.yesClaim.Item.Data", 0);
            config.set("GUI.Rewards.test.Item.yesClaim.Item.skullData", "%player_name%");
            config.set("GUI.Rewards.test.Item.yesClaim.Item.amount", 1);
            config.set("GUI.Rewards.test.Item.yesClaim.Title", "&aDaily reward");
            config.set("GUI.Rewards.test.Item.yesClaim.Lore", "\n&fClick to claim");
            config.set("GUI.Rewards.test.Item.yesClaim.Slot", 10);
            config.set("GUI.Rewards.test.Item.yesClaim.Permission", "");
            config.set("GUI.Rewards.test.Commands", new ArrayList<>(Arrays.asList("give %player_name% diamond 64", "give %player_name% golden_apple 2")));
            config.set("GUI.Rewards.test.Cooldown", 86400);

            config.set("GUI.Rewards.test.Item.noClaim.Item.Material", "MINECART");
            config.set("GUI.Rewards.test.Item.noClaim.Item.Data", 0);
            config.set("GUI.Rewards.test.Item.noClaim.Item.skullData", "%player_name%");
            config.set("GUI.Rewards.test.Item.noClaim.Item.amount", 1);
            config.set("GUI.Rewards.test.Item.noClaim.Title", "&cDaily reward");
            config.set("GUI.Rewards.test.Item.noClaim.Lore", "\n&7Come back again in &f%creward_time%");
            config.set("GUI.Rewards.test.Item.noClaim.Slot", 10);

            config.set("GUI.Rewards.test.Item.noPermClaim.Item.Material", "BARRIER");
            config.set("GUI.Rewards.test.Item.noPermClaim.Item.Data", 0);
            config.set("GUI.Rewards.test.Item.noPermClaim.Item.skullData", "%player_name%");
            config.set("GUI.Rewards.test.Item.noPermClaim.Item.amount", 1);
            config.set("GUI.Rewards.test.Item.noPermClaim.Title", "&cDaily reward");
            config.set("GUI.Rewards.test.Item.noPermClaim.Lore", "\n&cNo permission");
            config.set("GUI.Rewards.test.Item.noPermClaim.Slot", 10);

            ItemStack item = new ItemStack(Material.IRON_INGOT);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("&aItem 1");
            meta.setLore( new ArrayList<>(Arrays.asList("&cLore", "&eLore2")));
            item.setItemMeta(meta);

            config.set("GUI.otherItems.item1.Item", item);
            config.set("GUI.otherItems.item1.Slot", 12);

        }
        if (!Main.Legacy()) {
            config.set("Messages.Prefix", "&8[&2CReward&8] &r");
            config.set("Messages.claim", "&aReward claimed!");
            config.set("Messages.noClaim", "&cYou can not claim this reward yet! &7(%creward_time%)");
            config.set("Messages.configReload", "&7Config reloaded");
            config.set("Messages.reset", "&7%player_name%'s reward time has been reseted");
            config.set("Messages.noPerm", "&cYou do not have permission to execute this command");
            config.set("Messages.readyToClaim", "&aReady to claim!");
            config.set("Messages.notify", "&7You can claim &a%creward_amount% &7reward(s)!");
            config.set("Messages.autoPickup", "&7Auto-Claimed &a%rewards% &7reward(s) (Premium)");
            config.set("Autopickup.enabled", true);
            config.set("Autopickup.permission", "creward.autoclaim");

            config.set("GUI.title", "&8Daily reward");
            config.set("GUI.rows", 5);

            config.set("GUI.Filler.Enable", true);
            config.set("GUI.Filler.Slots", new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4)));
            config.set("GUI.Filler.Item.Material", "GRAY_STAINED_GLASS_PANE");

            config.set("Sounds.OPEN_MENU.Sound", "BLOCK_CHEST_OPEN");
            config.set("Sounds.OPEN_MENU.Volume", 1);
            config.set("Sounds.OPEN_MENU.Pitch", 4);

            config.set("Sounds.REWARD_PICKUP.Sound", "ENTITY_CHICKEN_EGG");
            config.set("Sounds.REWARD_PICKUP.Volume", 1);
            config.set("Sounds.REWARD_PICKUP.Pitch", 4);

            config.set("GUI.Rewards.test.Item.yesClaim.Item.Material", "CHEST_MINECART");
            config.set("GUI.Rewards.test.Item.yesClaim.Item.Data", 0);
            config.set("GUI.Rewards.test.Item.yesClaim.Item.skullData", "%player_name%");
            config.set("GUI.Rewards.test.Item.yesClaim.Item.amount", 1);
            config.set("GUI.Rewards.test.Item.yesClaim.Title", "&aDaily reward");
            config.set("GUI.Rewards.test.Item.yesClaim.Lore", "\n&fClick to claim");
            config.set("GUI.Rewards.test.Item.yesClaim.Slot", 10);
            config.set("GUI.Rewards.test.Item.yesClaim.Permission", "");
            config.set("GUI.Rewards.test.Commands", new ArrayList<>(Arrays.asList("give %player_name% diamond 64", "give %player_name% golden_apple 2")));
            config.set("GUI.Rewards.test.Cooldown", 86400);

            config.set("GUI.Rewards.test.Item.noClaim.Item.Material", "MINECART");
            config.set("GUI.Rewards.test.Item.noClaim.Item.Data", 0);
            config.set("GUI.Rewards.test.Item.noClaim.Item.skullData", "%player_name%");
            config.set("GUI.Rewards.test.Item.noClaim.Item.amount", 1);
            config.set("GUI.Rewards.test.Item.noClaim.Title", "&cDaily reward");
            config.set("GUI.Rewards.test.Item.noClaim.Lore", "\n&7Come back again in &f%creward_time%");
            config.set("GUI.Rewards.test.Item.noClaim.Slot", 10);

            config.set("GUI.Rewards.test.Item.noPermClaim.Item.Material", "BARRIER");
            config.set("GUI.Rewards.test.Item.noPermClaim.Item.Data", 0);
            config.set("GUI.Rewards.test.Item.noPermClaim.Item.skullData", "%player_name%");
            config.set("GUI.Rewards.test.Item.noPermClaim.Item.amount", 1);
            config.set("GUI.Rewards.test.Item.noPermClaim.Title", "&cDaily reward");
            config.set("GUI.Rewards.test.Item.noPermClaim.Lore", "\n&cNo permission");
            config.set("GUI.Rewards.test.Item.noPermClaim.Slot", 10);

            ItemStack item = new ItemStack(Material.IRON_INGOT);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("&aItem 1");
            meta.setLore( new ArrayList<>(Arrays.asList("&cLore", "&eLore2")));
            item.setItemMeta(meta);

            config.set("GUI.otherItems.item1.Item", item);
            config.set("GUI.otherItems.item1.Slot", 12);


        }
    }
    public void loadVars(){
        try {
            prefix = Main.Color(config.getString("Messages.Prefix"));
            yesClaim = Main.Color(config.getString("Messages.claim"));
            noClaim = Main.Color(config.getString("Messages.noClaim"));
            configReload = Main.Color(config.getString("Messages.configReload"));
            reset = Main.Color(config.getString("Messages.reset"));
            noPerm = Main.Color(config.getString("Messages.noPerm"));
            readyToClaim = Main.Color(config.getString("Messages.readyToClaim"));
            notify = Main.Color(config.getString("Messages.notify"));
            noPermInGUI = Main.Color(config.getString("Messages.noPermInGUI"));
            autoClaim = Main.Color(config.getString("Messages.autoPickup"));

            autoPickup = config.getBoolean("Autopickup.enabled");
            autoPickupPerm = config.getString("Autopickup.permission");

            guiTitle = Main.Color(config.getString("GUI.title"));
            guiRows = config.getInt("GUI.rows");

            guiFillerEnable = config.getBoolean("GUI.Filler.Enable");
            guiFillerItem = Material.getMaterial(config.getString("GUI.Filler.Item.Material"));
            if (Main.Legacy()) {
                guiFillerItemData = config.getInt("GUI.Filler.Item.Data");
            }
            guiFillerSlots = config.getIntegerList("GUI.Filler.Slots");

            if(Main.getPlugin().getSound(config.getString("Sounds.OPEN_MENU.Sound")) == null || Main.getPlugin().getSound(config.getString("Sounds.REWARD_PICKUP.Sound")) == null){
                Main.getPlugin().getLogger().severe("[CREWARD] Error when parsing sounds");
                Main.getPlugin().disable();
            }

            openSound = Sound.valueOf(config.getString("Sounds.OPEN_MENU.Sound"));
            pickupSound = Sound.valueOf(config.getString("Sounds.REWARD_PICKUP.Sound"));

            openVolume = config.getInt("Sounds.OPEN_MENU.Volume");
            openPitch = config.getInt("Sounds.OPEN_MENU.Pitch");

            pickupVolume = config.getInt("Sounds.REWARD_PICKUP.Volume");
            pickupPitch = config.getInt("Sounds.REWARD_PICKUP.Pitch");

            otherItems.clear();
            ConfigurationSection section = config.getConfigurationSection("GUI.otherItems");
            if (config.get("GUI.otherItems") != null) {
                if (section != null) {
                    for (String key : section.getKeys(false)) {
                        ItemStack item = config.getItemStack("GUI.otherItems." + key + ".Item");
                        Integer slot = config.getInt("GUI.otherItems." + key + ".Slot");
                        otherItems.put(item, slot);
                    }
                }
            }
        }catch (Exception e){
            System.out.println("Error while loading Config!");
            loadVars();
        }
    }

    public String getPrefix () {
        return prefix;
    }

    public String getYesClaim () {
        return yesClaim;
    }

    public String getNoClaim () {
        return noClaim;
    }

    public String getGuiTitle () {
        return guiTitle;
    }

    public int getGuiRows () {
        return guiRows;
    }

    public boolean isGuiFillerEnable () {
        return guiFillerEnable;
    }

    public List<Integer> getGuiFillerSlots () {
        return guiFillerSlots;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public String getConfigReload() {
        return configReload;
    }

    public String getReset() {
        return reset;
    }

    public String getNoPerm() {
        return noPerm;
    }

    public Material getGuiFillerItem() {
        return guiFillerItem;
    }

    public int getGuiFillerItemData() {
        return guiFillerItemData;
    }

    public HashMap<ItemStack, Integer> getOtherItems() {
        return otherItems;
    }

    public String getReadyToClaim() {
        return readyToClaim;
    }

    public String getNotify() {
        return notify;
    }

    public String getNoPermInGUI() {
        return noPermInGUI;
    }

    public String getAutoClaim() {
        return autoClaim;
    }

    public int getOpenVolume() {
        return openVolume;
    }

    public int getOpenPitch() {
        return openPitch;
    }

    public int getPickupVolume() {
        return pickupVolume;
    }

    public int getPickupPitch() {
        return pickupPitch;
    }

    public Sound getOpenSound() {
        return openSound;
    }

    public Sound getPickupSound() {
        return pickupSound;
    }

    public boolean isAutoPickup() {
        return autoPickup;
    }

    public String getAutoPickupPerm() {
        return autoPickupPerm;
    }
}

