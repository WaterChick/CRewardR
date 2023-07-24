package cz.waterchick.creward.managers.reward;

import cz.waterchick.creward.CItem;
import cz.waterchick.creward.enums.ErrorType;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class Reward {

    private String name;

    private String permission;

    private List<String> commands;

    private Integer cooldown;

    private CItem noCItem;
    private CItem yesCItem;
    private CItem noPermCItem;

    private int slot;




    public Reward(ConfigurationSection section, String name){
        this.name = name;
        for(String key : section.getKeys(false)){
            if(key.equals("Item")){
                for(String claim : section.getConfigurationSection("Item").getKeys(false)) {
                    if(claim.equalsIgnoreCase("yesClaim")) {
                        ConfigurationSection yesSection = section.getConfigurationSection("Item" + ".yesClaim.Item");
                        this.yesCItem = new CItem(yesSection);
                    }
                    if(claim.equalsIgnoreCase("noClaim")) {
                        ConfigurationSection noSection = section.getConfigurationSection("Item" + ".noClaim.Item");
                        this.noCItem = new CItem(noSection);
                    }
                    if(claim.equalsIgnoreCase("noPermClaim")) {
                        ConfigurationSection noPermSection = section.getConfigurationSection("Item" + ".noPermClaim.Item");
                        this.noPermCItem = new CItem(noPermSection);
                    }
                }
            }if(key.equals("Commands")){
                this.commands = section.getStringList(key);
            }if(key.equals("Cooldown")){
                this.cooldown = section.getInt(key);
            }
            if(key.equals("Slot")){
                this.slot = section.getInt(key);
            }
            if(key.equals("Permission")){
                this.permission = section.getString("Permission");
            }
        }
    }

    public String getName() {
        return name;
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

    public CItem getCItem(ErrorType errorType){
        switch (errorType){
            case SUCC:
                return yesCItem;
            case NOPERM:
                return noPermCItem;
            default:
                return noCItem;
        }
    }

    public int getSlot() {
        return slot;
    }
}
