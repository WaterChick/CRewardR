package cz.waterchick.creward.CReward.managers;

import cz.waterchick.creward.CReward.Main;
import cz.waterchick.creward.CReward.Reward;
import cz.waterchick.creward.CReward.managers.configurations.PluginConfig;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DBManager {

    private Boolean enable;
    private String username;
    private String password; // Enter your password for the db
    private String db;
    private String adress;
    private String url;

    private static Connection connection;
    private PluginConfig pluginConfig;

    public DBManager(PluginConfig pluginConfig){
        this.pluginConfig = pluginConfig;
        this.enable = pluginConfig.getEnable();
        this.username = pluginConfig.getUsername();
        this.password = pluginConfig.getPassword();
        this.db = pluginConfig.getDb();
        this.adress = pluginConfig.getAdress();
        this.url = "jdbc:mysql://"+adress+"/"+db;
        if(enable) {
            connStart();
            Main.getPlugin().getLogger().info("Using MYSQL to save Reward times!");
        }else{
            Main.getPlugin().getLogger().info("Using FILE to save Reward times!");
        }
    }

    private void connStart(){
        try { // try catch to get any SQL errors (for example connections errors)
            connection = DriverManager.getConnection(url, username, password);
            Main.getPlugin().getLogger().info("Database connection established!");
            createDecreaser();
            // with the method getConnection() from DriverManager, we're trying to set
            // the connection's url, username, password to the variables we made earlier and
            // trying to get a connection at the same time. JDBC allows us to do this.
        } catch (SQLException e) { // catching errors
            Main.getPlugin().getLogger().severe("There are some problems connecting to your database!");
            e.printStackTrace(); // prints out SQLException errors to the console (if any)
        }
    }

    public void connStop(){
        if(MySQLIsConnected()) {
            try {
                connection.close();
                Main.getPlugin().getLogger().info("Database connection closed!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void createDecreaser(){
        if(MySQLIsConnected()) {
            String sql = "CREATE TABLE IF NOT EXISTS data(decrease TINYINT(1));";
            try {
                PreparedStatement stmt = connection.prepareStatement(sql);
                // use executeUpdate() to update the databases table.
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public void createTable(Reward reward){
        if(MySQLIsConnected()) {
            String name = reward.getName();
            String sql = "CREATE TABLE IF NOT EXISTS " + name + "(uuid VARCHAR(36), time INT);";
            try {
                PreparedStatement stmt = connection.prepareStatement(sql);
                // use executeUpdate() to update the databases table.
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public boolean insertDecreaseTable(boolean bool){
        if(MySQLIsConnected()) {
            String sql = "";
            if(!checkIfSetDecreaser()){
                sql = "INSERT INTO data(decrease) VALUES(?)";
            }else{
                sql = "update data set decrease=?";
            }
            try {
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setBoolean(1, bool);
                stmt.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public void insertTable(UUID uuid, Integer i, Reward reward){
        if(MySQLIsConnected()) {
            String uuidS = uuid.toString().replaceAll("-", "_");
            String name = reward.getName();
            String sql = "";
            if(!checkIfSet(uuid,reward)){
                sql = "INSERT INTO " + name + "(uuid,time) VALUES('"+uuidS+"', '"+i+"')";
            }else {
                sql = "update " + name + " set time=" + i + " where uuid='" + uuidS + "'";
            }
            try {
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Integer getTime(UUID uuid,Reward reward){
        if(MySQLIsConnected()) {
            Integer b = 0;
            String uuidS = uuid.toString().replaceAll("-", "_");
            String name = reward.getName();
            String sql = "SELECT * FROM " + name + " WHERE " + "uuid=?;"; // Note the question mark as placeholders for input values
            try {
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, uuidS);// Set first "?" to query string
                ResultSet results = stmt.executeQuery();
                if (!results.next()) {
                } else {
                    b = results.getInt("time");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return b;
        }
        return null;
    }

    public boolean MySQLIsConnected() {
        return connection != null;
    }

    public boolean checkIfSet(UUID uuid,Reward reward) {
        if(MySQLIsConnected()) {
            String uuidS = uuid.toString().replaceAll("-", "_");
            String name = reward.getName();
            //"INSERT INTO " + name + "(uuid,time) VALUES('"+uuidS+"', '"+i+"') ON DUPLICATE KEY UPDATE time='"+i+"'";
            String sql = "SELECT * FROM "+name+" WHERE uuid = ?";
            try {
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, uuidS);
                ResultSet resultSet = stmt.executeQuery();
                if (!resultSet.next()) return false; else return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }
        return false;
    }

    public boolean checkIfSetDecreaser() {
        if(MySQLIsConnected()) {
            //"INSERT INTO " + name + "(uuid,time) VALUES('"+uuidS+"', '"+i+"') ON DUPLICATE KEY UPDATE time='"+i+"'";
            String sql = "SELECT * FROM data";
            try {
                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet resultSet = stmt.executeQuery();
                if (!resultSet.next()) return false; else return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }
        return false;
    }

    public Connection getConnection() {
        return connection;
    }

    public Boolean getDecrease(){
        if(MySQLIsConnected()) {
            boolean b = false;
            String sql = "SELECT * FROM data"; // Note the question mark as placeholders for input values
            try {
                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet results = stmt.executeQuery();
                if (!results.next()) {
                } else {
                    b = results.getBoolean("decrease");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return b;
        }
        return null;
    }
}
