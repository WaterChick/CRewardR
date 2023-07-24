package cz.waterchick.creward.dependencies;

import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class HDBAPI implements Listener {

    private static HeadDatabaseAPI api;

    @EventHandler
    public void onDatabaseLoad(DatabaseLoadEvent e) {
        api = new HeadDatabaseAPI();
    }

    public static ItemStack getHead(String id){
        return api.getItemHead(id);
    }




}
