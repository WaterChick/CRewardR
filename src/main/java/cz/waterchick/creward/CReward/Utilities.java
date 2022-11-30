package cz.waterchick.creward.CReward;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {

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
}
