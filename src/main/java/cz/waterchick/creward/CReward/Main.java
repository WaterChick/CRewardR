package cz.waterchick.creward.CReward;

import cz.waterchick.creward.CReward.enums.TimeFormat;
import cz.waterchick.creward.CReward.managers.ClassManager;
import cz.waterchick.creward.CReward.managers.DBManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Main extends JavaPlugin {

    private static Main plugin;
    private ClassManager classManager;
    private boolean papiEnabled;

    private boolean decrease;


    @Override
    public void onEnable() {
        plugin = this;
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getLogger().info("PlaceholderAPI registered & hooked!");
            papiEnabled = true;
        }else{
            papiEnabled = false;
        }
        classManager = new ClassManager();
        new UpdateChecker(this,100822).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                getLogger().info("Plugin up to date! ("+version+")");
            } else {
                getLogger().warning("There is a new version available! ("+version+")");
            }
        });
        start();

    }

    public static Main getPlugin() {
        return plugin;
    }

    @Override
    public void onDisable() {
        if(classManager != null) {
            classManager.getDataConfig().saveConfig();
            classManager.getDbManager().insertDecreaseTable(false);
            classManager.getDbManager().connStop();
        }

    }

    public void start() {
        if (!getPlugin().isDisabled()) {
            FileConfiguration config = classManager.getDataConfig().getConfig();
            Bukkit.getScheduler().runTaskTimerAsynchronously(Main.plugin, new Runnable() {
                @Override
                public void run() {
                    if(classManager.getPluginConfig().getEnable()){
                        decrease();
                    }else{
                        if (config != null) {
                            for (String key : config.getConfigurationSection("").getKeys(false)) {
                                for (String uuid : config.getConfigurationSection(key + ".timeLeft").getKeys(false)) {
                                    int time = config.getInt(key + ".timeLeft" + "." + uuid);
                                    if (time > 0) {
                                        config.set(key + ".timeLeft" + "." + uuid, time - 1);
                                    }
                                }
                            }
                        }
                    }

                }
            },0, 20);
        }
    }



    public List<String> setPlaceholders(List<String> lore,Reward r,Player p){
        List<String> newlore = new ArrayList<>();
        for (String line : lore) {
            if (!getPlugin().isPapiEnabled()) {
                if (line.contains("%creward_time%")) {
                    line = line.replace("%creward_time%", classManager.getPlayerManager().getTime(r,p.getUniqueId(),TimeFormat.TOTAL));
                }
                if (line.contains("%creward_hours%")) {
                    line = line.replace("%creward_time%", classManager.getPlayerManager().getTime(r,p.getUniqueId(),TimeFormat.HOURS));
                }
                if (line.contains("%creward_minutes%")) {
                    line = line.replace("%creward_time%", classManager.getPlayerManager().getTime(r,p.getUniqueId(),TimeFormat.MINUTES));
                }
                if (line.contains("%creward_seconds%")) {
                    line = line.replace("%creward_time%", classManager.getPlayerManager().getTime(r,p.getUniqueId(),TimeFormat.SECONDS));
                }if (line.contains("%creward_amount%")) {
                    line = line.replace("%creward_amount%", String.valueOf(classManager.getPlayerManager().claimable(p.getUniqueId())));
                }
                newlore.add(line);
            } else {
                if (line.contains("%creward_time%")) {
                    line = line.replace("%creward_time%", "%creward_time_" + r.getName() + "%");
                }
                if (line.contains("%creward_hours%")) {
                    line = line.replace("%creward_hours%", "%creward_hours_" + r.getName() + "%");
                }
                if (line.contains("%creward_minutes%")) {
                    line = line.replace("%creward_minutes%", "%creward_minutes_" + r.getName() + "%");
                }
                if (line.contains("%creward_seconds%")) {
                    line = line.replace("%creward_seconds%", "%creward_seconds_" + r.getName() + "%");
                }
                newlore.add(PlaceholderAPI.setPlaceholders(p, line));
            }
        }
        return newlore;
    }

    public String setPlaceholders(String msg ,Reward r,Player p){
        if(!getPlugin().isPapiEnabled()){
            if (msg.contains("%creward_time%")) {
                msg = msg.replace("%creward_time%", classManager.getPlayerManager().getTime(r,p.getUniqueId(), TimeFormat.TOTAL));
            }
            if (msg.contains("%creward_hours%")) {
                msg = msg.replace("%creward_time%", classManager.getPlayerManager().getTime(r,p.getUniqueId(), TimeFormat.HOURS));
            }
            if (msg.contains("%creward_minutes%")) {
                msg = msg.replace("%creward_time%", classManager.getPlayerManager().getTime(r,p.getUniqueId(), TimeFormat.MINUTES));
            }
            if (msg.contains("%creward_seconds%")) {
                msg = msg.replace("%creward_time%", classManager.getPlayerManager().getTime(r,p.getUniqueId(), TimeFormat.SECONDS));
            }if (msg.contains("%creward_amount%")) {
                msg = msg.replace("%creward_amount%", String.valueOf(classManager.getPlayerManager().claimable(p.getUniqueId())));
            }
        }
        else {

            if (msg.contains("%creward_time%")) {
                msg = msg.replace("%creward_time%", "%creward_time_" + r.getName() + "%");
            }
            if (msg.contains("%creward_hours%")) {
                msg = msg.replace("%creward_hours%", "%creward_hours_" + r.getName() + "%");
            }
            if (msg.contains("%creward_minutes%")) {
                msg = msg.replace("%creward_minutes%", "%creward_minutes_" + r.getName() + "%");
            }
            if (msg.contains("%creward_seconds%")) {
                msg = msg.replace("%creward_seconds%", "%creward_seconds_" + r.getName() + "%");
            }
            msg = PlaceholderAPI.setPlaceholders(p, msg);
        }
        return msg;
    }

    public String setPlaceholders(String msg, Player p){
        if(isPapiEnabled()){
            msg = PlaceholderAPI.setPlaceholders(p,msg);
        }
        msg = msg.replaceAll("%player_name%",p.getName());
        msg = msg.replaceAll("%player%",p.getName());
        msg = msg.replaceAll("%creward_amount%",String.valueOf(classManager.getPlayerManager().claimable(p.getUniqueId())));
        return msg;
    }

    public boolean isDisabled(){
        if(!Bukkit.getPluginManager().isPluginEnabled(this)){
            return true;
        }
        return false;
    }

    public void disable(){
        Bukkit.getPluginManager().disablePlugin(this);
    }

    public Sound getSound(String sound){
        try{
            return Sound.valueOf(sound);
        }catch (Exception e){
            e.printStackTrace();
            disable();
        }
        return null;
    }

    public void decrease(){
        if(classManager.getDbManager().getDecrease() == null || !classManager.getDbManager().getDecrease()) {
            decrease = classManager.getDbManager().insertDecreaseTable(true);
        }
        if(decrease) {
            try {
                List<String> tables = new ArrayList<>();
                List<UUID> uuids = new ArrayList<>();
                String[] types = {"TABLE"};
                DatabaseMetaData md = classManager.getDbManager().getConnection().getMetaData();
                ResultSet rs = md.getTables(classManager.getDbManager().getConnection().getCatalog(), null, "%", types);
                while (rs.next()) {
                    String tableName = rs.getString(3);
                    tables.add(tableName);
                }
                rs.close();
                for (String table : tables) {
                    Reward reward = classManager.getRewardManager().getReward(table);
                    if(reward == null){
                        continue;
                    }
                    String sql = "SELECT * from " + table;
                    PreparedStatement stmt = classManager.getDbManager().getConnection().prepareStatement(sql);
                    ResultSet resultSet = stmt.executeQuery();
                    while (resultSet.next()) {
                        if(resultSet.getString("uuid") != null) {
                            uuids.add(UUID.fromString(resultSet.getString("uuid").replace("_", "-")));
                        }
                    }
                    resultSet.close();
                    for (UUID uuid : uuids) {
                        Integer timeLeft = classManager.getDbManager().getTime(uuid, reward);
                        if (timeLeft != 0) {
                            classManager.getDbManager().insertTable(uuid, timeLeft - 1, reward);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isPapiEnabled() {
        return papiEnabled;
    }
}
