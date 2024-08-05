package cz.waterchick.creward;

import cz.waterchick.creward.managers.ClassManager;
import cz.waterchick.creward.managers.DBManager;
import cz.waterchick.creward.managers.configurations.DataConfig;
import cz.waterchick.creward.managers.configurations.PluginConfig;
import cz.waterchick.creward.managers.reward.Reward;
import cz.waterchick.creward.managers.reward.RewardManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class CReward extends JavaPlugin {

    private static CReward plugin;
    private ClassManager classManager;

    private boolean decrease;


    @Override
    public void onEnable() {
        plugin = this;
        if (isPapiEnabled()) {
            getLogger().info("PlaceholderAPI registered & hooked!");
        }
        if(isHDBEnabled()){
            getLogger().info("Hooked to HeadDatabase!");
        }
        classManager = new ClassManager();
        new UpdateChecker(this,100822).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                Bukkit.getConsoleSender().sendMessage("[CReward] " + ChatColor.GREEN + "Plugin up to date! ("+version+")");
            } else {
                Bukkit.getConsoleSender().sendMessage("[CReward] " + ChatColor.DARK_GRAY + "-------------------------------");
                Bukkit.getConsoleSender().sendMessage("[CReward] " + ChatColor.RED + "There is a new version available! ("+version+")");
                Bukkit.getConsoleSender().sendMessage("[CReward] " + ChatColor.YELLOW + "Download it here: https://www.spigotmc.org/resources/100822");
                Bukkit.getConsoleSender().sendMessage("[CReward] " + ChatColor.DARK_GRAY + "-------------------------------");
            }
        });
        start();

    }

    public static CReward getPlugin() {
        return plugin;
    }

    @Override
    public void onDisable() {
        if(classManager != null) {
            DataConfig.getInstance().saveConfig();
            DBManager.getInstance().insertDecreaseTable(false);
            DBManager.getInstance().connStop();
        }

    }

    public void start() {
        if (!getPlugin().isDisabled()) {
            PluginConfig pluginConfig = PluginConfig.getInstance();
            DataConfig dataConfig = DataConfig.getInstance();
            FileConfiguration config = dataConfig.getConfig();
            Bukkit.getScheduler().runTaskTimerAsynchronously(CReward.plugin, new Runnable() {
                @Override
                public void run() {
                    if(pluginConfig.getEnable()){
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
        DBManager dbManager = DBManager.getInstance();
        RewardManager rewardManager = RewardManager.getInstance();
        if(dbManager.getDecrease() == null || !dbManager.getDecrease()) {
            decrease = dbManager.insertDecreaseTable(true);
        }
        if(decrease) {
            try {
                List<String> tables = DBManager.getInstance().getTables();
                List<UUID> uuids = new ArrayList<>();
                for (String table : tables) {
                    Reward reward = rewardManager.getReward(table);
                    if(reward == null){
                        continue;
                    }
                    String sql = "SELECT * from " + table;
                    PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql);
                    ResultSet resultSet = stmt.executeQuery();
                    while (resultSet.next()) {
                        if(resultSet.getString("uuid") != null) {
                            uuids.add(UUID.fromString(resultSet.getString("uuid").replace("_", "-")));
                        }
                    }
                    resultSet.close();
                    for (UUID uuid : uuids) {
                        Integer timeLeft = dbManager.getTime(uuid, reward);
                        if (timeLeft != 0) {
                            dbManager.insertTable(uuid, timeLeft - 1, reward);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isPapiEnabled() {
        return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }
    public boolean isHDBEnabled() {
        return Bukkit.getPluginManager().getPlugin("HeadDatabase") != null;
    }
}
