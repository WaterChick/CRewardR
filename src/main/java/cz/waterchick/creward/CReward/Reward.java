package cz.waterchick.creward.CReward;

import cz.waterchick.creward.CReward.Main;
import cz.waterchick.creward.CReward.enums.ErrorType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Reward {

    private String name;

    private String permission;

    private Integer yesSlot;

    private String yesSkullData;

    private Integer noSlot;

    private String noSkullData;

    private List<String> commands;

    private Integer cooldown;

    private Integer noPermSlot;

    private String noPermSkullData;

    private CItem noCItem;
    private CItem yesCItem;
    private CItem noPermCItem;




    public Reward(ConfigurationSection section, String name){
        this.name = name;
        for(String key : section.getKeys(false)){
            if(key.equals("Item")){
                for(String claim : section.getConfigurationSection("Item").getKeys(false)) {
                    if(claim.equalsIgnoreCase("yesclaim")) {
                        ConfigurationSection yesSection = section.getConfigurationSection("Item" + ".yesClaim");
                        if(yesSection == null){
                            Bukkit.getLogger().severe("[CReward] SECTION ERROR IN: " + name + "." + key);
                            Main.getPlugin().disable();
                            return;
                        }
                        Material mat = Material.getMaterial(yesSection.getString("Item.Material"));
                        int data = yesSection.getInt("Item.Data");
                        String skullData = yesSection.getString("Item.skullData");
                        yesSkullData = skullData;
                        Integer amount = yesSection.getInt("Item.amount");
                        String title = Utilities.Color(yesSection.getString("Title"));
                        Integer slot = yesSection.getInt("Slot");
                        String permission = yesSection.getString("Permission");
                        this.permission = permission;
                        List<String> lore = new ArrayList<>();
                        if (yesSection.getString("Lore") != null) {
                            for (String line : yesSection.getString("Lore").split("\n")) {
                                lore.add(Utilities.Color(line));
                            }

                        }
                        if (mat == null || amount == null || slot == null|| (Integer) data == null) {
                            Bukkit.getLogger().severe("[CReward] ERROR IN: " + name + "." + key + "." + claim);
                            Main.getPlugin().disable();
                            return;
                        }
                        this.yesSlot = slot;
                        this.yesCItem = new CItem(mat,amount,title,lore,(short) data);
                    }
                    if(claim.equalsIgnoreCase("noclaim")) {
                        ConfigurationSection noSection = section.getConfigurationSection("Item" + ".noClaim");
                        if(noSection == null){
                            Bukkit.getLogger().severe("[CReward] SECTION ERROR IN: " + name + "." + key);
                            Main.getPlugin().disable();
                            return;
                        }
                        Material mat = Material.getMaterial(noSection.getString("Item.Material"));
                        int data = noSection.getInt("Item.Data");
                        String skullData = noSection.getString("Item.skullData");
                        noSkullData = skullData;
                        Integer amount = noSection.getInt("Item.amount");
                        String title = Utilities.Color(noSection.getString("Title"));
                        Integer slot = noSection.getInt("Slot");
                        List<String> lore = new ArrayList<>();
                        if (noSection.getString("Lore") != null) {
                            for (String line : noSection.getString("Lore").split("\n")) {
                                lore.add(Utilities.Color(line));
                            }

                        }
                        if (mat == null || amount == null || slot == null|| (Integer) data == null) {
                            Bukkit.getLogger().severe("[CReward] ERROR IN: " + name + "." + key + "." + claim);
                            continue;
                        }
                        this.noSlot = slot;
                        this.noCItem = new CItem(mat,amount,title,lore,(short) data);
                    }
                    if(claim.equalsIgnoreCase("nopermclaim")) {
                        ConfigurationSection noPermSection = section.getConfigurationSection("Item" + ".noPermClaim");
                        if(noPermSection == null){
                            Bukkit.getLogger().severe("[CReward] SECTION ERROR IN: " + name + "." + key);
                            Main.getPlugin().disable();
                            return;
                        }
                        Material mat = Material.getMaterial(noPermSection.getString("Item.Material"));
                        int data = noPermSection.getInt("Item.Data");
                        String skullData = noPermSection.getString("Item.skullData");
                        noPermSkullData = skullData;
                        Integer amount = noPermSection.getInt("Item.amount");
                        String title = Utilities.Color(noPermSection.getString("Title"));
                        Integer slot = noPermSection.getInt("Slot");
                        List<String> lore = new ArrayList<>();
                        if (noPermSection.getString("Lore") != null) {
                            for (String line : noPermSection.getString("Lore").split("\n")) {
                                lore.add(Utilities.Color(line));
                            }

                        }
                        if (mat == null || amount == null || slot == null|| (Integer) data == null) {
                            Bukkit.getLogger().severe("[CReward] ERROR IN: " + name + "." + key + "." + claim);
                            Main.getPlugin().disable();
                            return;
                        }
                        this.noPermSlot = slot;
                        this.noPermCItem = new CItem(mat,amount,title,lore,(short) data);
                    }
                }
            }if(key.equals("Commands")){
                this.commands = section.getStringList(key);
            }if(key.equals("Cooldown")){
                this.cooldown = section.getInt(key);
            }
        }
    }

    public String getName() {
        return name;
    }

    public Integer getYesSlot() {
        return yesSlot;
    }

    public Integer getNoSlot() {
        return noSlot;
    }

    public List<String> getCommands() {
        return commands;
    }

    public String getPermission() {
        return permission;
    }

    public Integer getCooldown() {
        return cooldown;
    }

    public Integer getNoPermSlot() {
        return noPermSlot;
    }

    public CItem getItemStack(ErrorType errorType){
        switch (errorType){
            case SUCC:
                return yesCItem;
            case NOPERM:
                return noPermCItem;
            default:
                return noCItem;
        }
    }

    public Integer getSlot(ErrorType errorType){
        switch (errorType){
            case SUCC:
                return yesSlot;
            case NOPERM:
                return noPermSlot;
            default:
                return noSlot;
        }
    }

    public String getSkullData(ErrorType errorType){
        switch (errorType){
            case SUCC:
                return yesSkullData;
            case NOPERM:
                return noPermSkullData;
            default:
                return noSkullData;
        }
    }
}
