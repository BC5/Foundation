package uk.lsuth.mc.foundation.data;

import org.bson.Document;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.logging.Logger;

public abstract class DataManager
{
    ArrayList<PlayerDataWrapper> cachedPlayers;
    Logger log;
    Document playerTemplate;

    public DataManager()
    {
        cachedPlayers = new ArrayList<PlayerDataWrapper>();
        this.log = log;
    }

    public void setTemplate(Document playerTemplate)
    {
        this.playerTemplate = playerTemplate;
    }


    abstract PlayerDataWrapper loadPlayer(OfflinePlayer player);

    public abstract void unloadPlayer(OfflinePlayer player);

    public abstract void savePlayer(OfflinePlayer player);

    public PlayerDataWrapper fetchData(OfflinePlayer player)
    {
        for(PlayerDataWrapper d:cachedPlayers)
        {
            if(player.getUniqueId().equals(d.getUniqueId()))
            {
                return d;
            }
        }
        return loadPlayer(player);
    }

    public abstract PlayerDataWrapper fetchData(String name);

    public void stash()
    {
        log.finer("Stashing data");
        for(PlayerDataWrapper p:cachedPlayers)
        {
            savePlayer(p.getPlayer());
        }
    }

    public abstract boolean playerExists(OfflinePlayer player);

}
