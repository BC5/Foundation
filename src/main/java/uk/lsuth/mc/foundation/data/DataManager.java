package uk.lsuth.mc.foundation.data;

import org.bson.Document;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public abstract class DataManager
{
    ArrayList<PlayerDataWrapper> cachedPlayers;
    Logger log;
    Document playerTemplate;

    HashMap<String,Document> miscDocs;

    public DataManager(Logger log)
    {
        cachedPlayers = new ArrayList<PlayerDataWrapper>();
        miscDocs = new HashMap<String,Document>();
        this.log = log;
    }

    public void setTemplate(Document playerTemplate)
    {
        this.playerTemplate = playerTemplate;
    }

    public Document createPlayerTemplate(OfflinePlayer player)
    {
        log.info("Creating new player profile for "  + player.getName());
        Document playerdoc = new Document(playerTemplate);
        playerdoc.append("_id",player.getUniqueId().toString());
        playerdoc.append("name",player.getName());
        return playerdoc;
    }

    abstract PlayerDataWrapper loadPlayer(OfflinePlayer player);

    public abstract Document fetchMiscDoc(String name);

    public void registerMiscDoc(String name, Document doc)
    {
        miscDocs.put(name,doc);
        saveMiscDoc(name,doc);
    }

    public abstract void saveMiscDoc(String name, Document doc);

    public abstract boolean miscDocExists(String name);

    public void unloadPlayer(OfflinePlayer player)
    {
        PlayerDataWrapper pdw = fetchData(player);
        savePlayer(pdw);
        cachedPlayers.remove(pdw);
    }

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
        log.info("Stashing data");
        for(PlayerDataWrapper p:cachedPlayers)
        {
            savePlayer(p.getPlayer());
        }

        for(Map.Entry<String,Document> d:miscDocs.entrySet())
        {
            saveMiscDoc(d.getKey(),d.getValue());
        }

    }

    public abstract boolean playerExists(OfflinePlayer player);

    public abstract void savePlayer(PlayerDataWrapper pdw);

}
