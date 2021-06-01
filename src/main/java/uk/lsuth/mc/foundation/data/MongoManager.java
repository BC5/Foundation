package uk.lsuth.mc.foundation.data;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;
import java.util.logging.Logger;

import static com.mongodb.client.model.Filters.eq;

public class MongoManager extends DataManager
{

    String dbAddress;
    MongoClient mongo;
    MongoDatabase db;
    MongoCollection<Document> playerCollection;

    public MongoManager(String address, Logger log)
    {
        //Logger.getLogger("org.mongodb.driver").setLevel(Level.OFF);
        this.dbAddress = address;
        connect();
    }

    private void connect()
    {
        mongo = MongoClients.create(this.dbAddress);
        db = mongo.getDatabase("foundation");

        playerCollection = db.getCollection("players");
    }


    @Override
    public boolean playerExists(OfflinePlayer player)
    {
        return playerCollection.countDocuments(eq("_id", player.getUniqueId().toString())) != 0;
    }

    @Override
    public PlayerDataWrapper loadPlayer(OfflinePlayer player)
    {
        UUID uuid = player.getUniqueId();
        Document playerdoc = playerCollection.find(eq("_id",uuid.toString())).first();

        if(playerdoc == null)
        {
            log.info("Creating new player profile for "  + player.getName());
            playerdoc = new Document(playerTemplate);
            playerdoc.append("_id",player.getUniqueId().toString());
            playerdoc.append("name",player.getName());
            System.out.println(playerdoc.toJson());
            playerCollection.insertOne(playerdoc);

            return new PlayerDataWrapper(player,this,playerdoc,player.getUniqueId());
        }
        else
        {
            //Handle name change
            if(!playerdoc.getString("name").equals(player.getName()))
            {
                log.warning("Player " + playerdoc.getString("name") + " has changed their name to " + player.getName());
                playerdoc.put("oldName",playerdoc.getString("name"));
                playerdoc.replace("name",player.getName());
            }

            log.info("Player " + player.getName() + " loaded from database");
            PlayerDataWrapper wrapper = new PlayerDataWrapper(player,this,playerdoc,uuid);
            cachedPlayers.add(wrapper);
            return wrapper;
        }
    }

    @Override
    public PlayerDataWrapper fetchData(String name)
    {
        Document playerdoc = playerCollection.find(eq("name",name)).first();
        if(playerdoc == null)
        {
            log.warning("Player " + name + " has no database document");
            return null;
        }
        else
        {
            UUID playerUUID = UUID.fromString((String) playerdoc.get("_id"));
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
            PlayerDataWrapper dataWrapper = new PlayerDataWrapper(offlinePlayer,this,playerdoc,playerUUID);
            cachedPlayers.add(dataWrapper);
            return dataWrapper;
        }
    }

    @Override
    public void unloadPlayer(OfflinePlayer player)
    {
        String uuid = player.getUniqueId().toString();
        PlayerDataWrapper dw = fetchData(player);
        Document playerdoc = dw.getPlayerDocument();
        playerCollection.replaceOne(eq("_id",uuid),playerdoc);

        cachedPlayers.remove(dw);
    }




    @Override
    public void savePlayer(OfflinePlayer player)
    {
        String uuid = player.getUniqueId().toString();
        Document playerdoc = fetchData(player).getPlayerDocument();
        playerCollection.replaceOne(eq("_id",uuid),playerdoc);
    }

}
