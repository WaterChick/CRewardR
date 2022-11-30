package cz.waterchick.creward.CReward.managers.configurations;

import cz.waterchick.creward.CReward.Main;
import cz.waterchick.creward.CReward.Utilities;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
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
    private boolean closeOnClaim;

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

    private Boolean enable;
    private String username;
    private String password;
    private String adress;
    private String db;

    private HashMap<ItemStack, Integer> otherItems = new HashMap<>();


    public PluginConfig() {
        createConfig();
    }

    public void createConfig() {
        file = new File(Main.getPlugin().getDataFolder(), "1.16/config.yml");
        if(Utilities.Legacy()){
            file = new File(Main.getPlugin().getDataFolder(), "1.8/config.yml");
        }
        if (!file.exists()) {
            if(Utilities.Legacy()) {
                Main.getPlugin().saveResource("1.8/config.yml",false);
            }else{
                Main.getPlugin().saveResource("1.16/config.yml",false);
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        loadVars();
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(file);
        loadVars();
    }


    public void loadVars(){
        try {
            prefix = Utilities.Color(config.getString("Messages.Prefix"));
            yesClaim = Utilities.Color(config.getString("Messages.claim"));
            noClaim = Utilities.Color(config.getString("Messages.noClaim"));
            configReload = Utilities.Color(config.getString("Messages.configReload"));
            reset = Utilities.Color(config.getString("Messages.reset"));
            noPerm = Utilities.Color(config.getString("Messages.noPerm"));
            readyToClaim = Utilities.Color(config.getString("Messages.readyToClaim"));
            notify = Utilities.Color(config.getString("Messages.notify"));
            autoClaim = Utilities.Color(config.getString("Messages.autoPickup"));
            closeOnClaim = config.getBoolean("closeOnClaim");

            autoPickup = config.getBoolean("Autopickup.enabled");
            autoPickupPerm = config.getString("Autopickup.permission");

            guiTitle = Utilities.Color(config.getString("GUI.title"));
            guiRows = config.getInt("GUI.rows");

            guiFillerEnable = config.getBoolean("GUI.Filler.Enable");
            guiFillerItem = Material.getMaterial(config.getString("GUI.Filler.Item.Material"));
            guiFillerItemData = config.getInt("GUI.Filler.Item.Data");
            guiFillerSlots = config.getIntegerList("GUI.Filler.Slots");

            if(Main.getPlugin().getSound(config.getString("Sounds.OPEN_MENU.Sound")) == null || Main.getPlugin().getSound(config.getString("Sounds.REWARD_PICKUP.Sound")) == null){
                Main.getPlugin().getLogger().severe("Error while parsing sounds");
                Main.getPlugin().disable();
                return;
            }

            openSound = Sound.valueOf(config.getString("Sounds.OPEN_MENU.Sound"));
            pickupSound = Sound.valueOf(config.getString("Sounds.REWARD_PICKUP.Sound"));

            openVolume = config.getInt("Sounds.OPEN_MENU.Volume");
            openPitch = config.getInt("Sounds.OPEN_MENU.Pitch");

            pickupVolume = config.getInt("Sounds.REWARD_PICKUP.Volume");
            pickupPitch = config.getInt("Sounds.REWARD_PICKUP.Pitch");

            enable = config.getBoolean("MySQL.enable");
            username = config.getString("MySQL.username");
            password = config.getString("MySQL.password");
            db = config.getString("MySQL.db");
            adress = config.getString("MySQL.adress");

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
            Main.getPlugin().getLogger().severe("Error while loading Config! Don't forget to check material IDs and Sounds");
            Main.getPlugin().disable();
            e.printStackTrace();
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

    public boolean isCloseOnClaim() {
        return closeOnClaim;
    }

    public Boolean getEnable() {
        return enable;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getAdress() {
        return adress;
    }

    public String getDb() {
        return db;
    }
}

