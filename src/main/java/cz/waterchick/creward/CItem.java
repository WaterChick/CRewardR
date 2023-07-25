package cz.waterchick.creward;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CItem {

    private Material material;
    private String title;
    private List<String> lore;
    private int data;

    private boolean glow;
    private Integer amount;
    private String skullData;

    public CItem(ConfigurationSection section) {
        if(section == null){
            return;
        }
        Material mat = Material.getMaterial(section.getString("Material"));
        int data = section.getInt("Data");
        String skullData = section.getString("skullData");
        boolean glow = section.getBoolean("Glow");
        Integer amount = section.getInt("Amount");
        String title = Utilities.Color(section.getString("Title"));
        List<String> lore = new ArrayList<>();
        if (section.getString("Lore") != null) {
            for (String line : section.getString("Lore").split("\n")) {
                lore.add(Utilities.Color(line));
            }

        }
        if (mat == null || amount == null) {
            return;
        }
        this.material = mat;
        this.skullData = skullData;
        this.amount = amount;
        this.title = title;
        this.lore = lore;
        this.data = data;
        this.glow = glow;
    }

    public ItemStack getItemStack(){
        ItemStack itemStack;
        if(material == null){
            return new ItemStack(Material.BEDROCK);
        }
        if (data != 0){
            itemStack = new ItemStack(material, 1, (short) data);
        }else{
            itemStack = new ItemStack(material, 1);
        }
        itemStack.setAmount(amount);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(title);
        meta.setLore(lore);

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public String getSkullData() {
        return skullData;
    }

    public boolean isGlow(){
        return glow;
    }
}
