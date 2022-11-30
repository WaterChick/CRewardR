package cz.waterchick.creward.CReward;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class CItem {

    private final Material material;
    private final String title;
    private final List<String> lore;
    private final int data;
    private final Integer amount;

    public CItem(Material material,Integer amount, String title, List<String> lore, int data) {
        this.material = material;
        this.amount = amount;
        this.title = title;
        this.lore = lore;
        this.data = data;
    }

    public ItemStack getItemStack(){
        ItemStack itemStack;
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




}
