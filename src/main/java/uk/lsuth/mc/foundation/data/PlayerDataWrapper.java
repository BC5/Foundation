package uk.lsuth.mc.foundation.data;

import org.bson.Document;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class PlayerDataWrapper
{
    private OfflinePlayer player;
    private Document playerDocument;

    private UUID uuid;

    public PlayerDataWrapper(OfflinePlayer player, DataManager dataManager, Document playerDocument, UUID uuid)
    {
        this.player = player;
        this.playerDocument = playerDocument;
        this.uuid = uuid;
    }

    public void save()
    {
        //TODO:
    }

    public UUID getUniqueId()
    {
        return uuid;
    }

    public Document getPlayerDocument()
    {
        return playerDocument;
    }

    public OfflinePlayer getPlayer()
    {
        return player;
    }
}
