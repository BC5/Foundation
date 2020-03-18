package uk.lsuth.mc.foundation.data;

import org.bson.Document;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface DataManager
{
    PlayerDataWrapper loadPlayer(OfflinePlayer player);

    void unloadPlayer(OfflinePlayer player);

    void savePlayer(OfflinePlayer player);

    void stash();

    boolean playerExists(OfflinePlayer player);

    PlayerDataWrapper fetchData(OfflinePlayer player);

    /**
     * Fetches a database
     *
     * @param playerName
     * @return
     */
    PlayerDataWrapper fetchData(String playerName);

}
