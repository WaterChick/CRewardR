package cz.waterchick.creward.CReward;

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
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

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

    private boolean decrease;


    @Override
    public void onEnable() {
        plugin = this;
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().severe("Could not find PlaceholderAPI! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        classManager = new ClassManager();
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

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.getOpenInventory().getTitle().equals(PlaceholderAPI.setPlaceholders(p, classManager.getPluginConfig().getGuiTitle()))) {
                            Inventory inv = p.getOpenInventory().getTopInventory();
                            for (int i = 0; i < inv.getSize(); i++) {
                                if (inv.getItem(i) == null) {
                                    continue;
                                }
                                ItemStack invItem = inv.getItem(i).clone();
                                if (invItem.getType() == Material.AIR) {
                                    continue;
                                }
                                if (invItem.getType() == classManager.getPluginConfig().getGuiFillerItem()) {
                                    continue;
                                }
                                UUID uuid = p.getUniqueId();
                                Reward r = classManager.getRewardManager().getReward(i);
                                if (r == null) {
                                    continue;
                                }
                                ItemStack item = classManager.getGui().getRewardItem(r, uuid).clone();
                                if (!item.getType().toString().equalsIgnoreCase("LEGACY_SKULL_ITEM") && !item.getType().toString().equalsIgnoreCase("SKULL_ITEM")) {
                                    ItemMeta meta = item.getItemMeta();
                                    meta.setLore(setPlaceholders(meta.getLore(), r, p));
                                    item.setItemMeta(meta);
                                    inv.setItem(i, item);
                                }
                            }
                            p.updateInventory();
                        }
                    }

                }
            },0, 20);
        }
    }

    public static boolean Legacy(){
        String version = Bukkit.getVersion();
        if(version.contains("1.8")){
            return true;
        }
        if(version.contains("1.9")){
            return true;
        }
        if(version.contains("1.10")){
            return true;
        }
        if(version.contains("1.11")){
            return true;
        }
        if(version.contains("1.12")){
            return true;
        }
        if(version.contains("1.13")){
            return false;
        }
        if(version.contains("1.14")){
            return false;
        }
        if(version.contains("1.15")){
            return false;
        }
        if(version.contains("1.16")){
            return false;
        }
        if(version.contains("1.17")){
            return false;
        }
        if(version.contains("1.18")){
            return false;
        }
        return false;
    }

    public static boolean LegacySound(){
        String version = Bukkit.getVersion();
        if(version.contains("1.8")){
            return true;
        }
        if(version.contains("1.9")){
            return false;
        }
        if(version.contains("1.10")){
            return false;
        }
        if(version.contains("1.11")){
            return false;
        }
        if(version.contains("1.12")){
            return false;
        }
        if(version.contains("1.13")){
            return false;
        }
        if(version.contains("1.14")){
            return false;
        }
        if(version.contains("1.15")){
            return false;
        }
        if(version.contains("1.16")){
            return false;
        }
        if(version.contains("1.17")){
            return false;
        }
        if(version.contains("1.18")){
            return false;
        }
        return false;
    }

    public static String Color(String message) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String hexCode = message.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');

            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder("");
            for (char c : ch) {
                builder.append("&" + c);
            }

            message = message.replace(hexCode, builder.toString());
            matcher = pattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static List<String> setPlaceholders(List<String> lore,Reward r,Player p){
        List<String> newlore = new ArrayList<>();
        for (String line : lore) {
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
        return newlore;
    }

    public static String setPlaceholders(String msg ,Reward r,Player p){
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
        return PlaceholderAPI.setPlaceholders(p, msg);
    }

    public ClassManager getClassManager() {
        return classManager;
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
                DatabaseMetaData dbmd = classManager.getDbManager().getConnection().getMetaData();
                String[] types = {"TABLE"};
                ResultSet rs = dbmd.getTables(null, null, "%", types);
                while (rs.next()) {
                    if(!rs.getString("TABLE_NAME").equals("data")) {
                        tables.add(rs.getString("TABLE_NAME"));
                    }
                }
                for (String table : tables) {
                    String sql = "SELECT * from " + table;
                    PreparedStatement stmt = classManager.getDbManager().getConnection().prepareStatement(sql);
                    ResultSet resultSet = stmt.executeQuery();
                    while (resultSet.next()) {
                        if(resultSet.getString("uuid") != null) {
                            uuids.add(UUID.fromString(resultSet.getString("uuid").replace("_", "-")));
                        }
                    }
                    for (UUID uuid : uuids) {
                        Reward reward = classManager.getRewardManager().getReward(table);
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
}
