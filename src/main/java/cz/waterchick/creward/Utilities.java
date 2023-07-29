package cz.waterchick.creward;

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
        return false;
    }

    public static String Color(String message) {
        if(message == null){
            return message;
        }
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
