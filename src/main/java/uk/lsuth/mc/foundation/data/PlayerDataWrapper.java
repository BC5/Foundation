package uk.lsuth.mc.foundation.data;

import org.bson.Document;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class PlayerDataWrapper
{
    private final OfflinePlayer player;
    private final Document playerDocument;

    private final UUID uuid;

    public PlayerDataWrapper(OfflinePlayer player, Document playerDocument, UUID uuid)
    {
        this.player = player;
        this.playerDocument = playerDocument;
        this.uuid = uuid;
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
