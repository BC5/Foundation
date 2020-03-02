package uk.lsuth.mc.foundation.data;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

//Filters i.e. eq(x,y)
import static com.mongodb.client.model.Filters.*;

import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoManager implements DataManager
{

    String dbAddress;
    MongoClient mongo;
    MongoDatabase db;

    ArrayList<PlayerDataWrapper> cachedPlayers;
    MongoCollection<Document> playerCollection;

    Logger log;
    Document playerTemplate;

    public MongoManager(String address, Document playerTemplate, Logger log)
    {
        //Logger.getLogger("org.mongodb.driver").setLevel(Level.OFF);
        cachedPlayers = new ArrayList<PlayerDataWrapper>();
        this.dbAddress = address;
        this.playerTemplate = playerTemplate;
        this.log = log;
        connect();
    }

    private void connect()
    {
        mongo = MongoClients.create(this.dbAddress);
        db = mongo.getDatabase("foundation");

        playerCollection = db.getCollection("players");
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
            log.info("Player" + player.getName() + "loaded from database");
            PlayerDataWrapper wrapper = new PlayerDataWrapper(player,this,playerdoc,uuid);
            cachedPlayers.add(wrapper);
            return wrapper;
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

    @Override
    public void stash()
    {
        for(PlayerDataWrapper p:cachedPlayers)
        {
            savePlayer(p);
        }
    }

    @Override
    public void savePlayer(OfflinePlayer player)
    {
        String uuid = player.getUniqueId().toString();
        Document playerdoc = fetchData(player).getPlayerDocument();
        playerCollection.replaceOne(eq("_id",uuid),playerdoc);
    }

    public void savePlayer(PlayerDataWrapper player)
    {
        String uuid = player.getUniqueId().toString();
        Document playerdoc = player.getPlayerDocument();
        playerCollection.replaceOne(eq("_id",uuid),playerdoc);
    }

    @Override
    public boolean playerExists(OfflinePlayer player)
    {
        if(playerCollection.countDocuments(eq("_id",player.getUniqueId().toString())) == 0)
        {
            return false;
        }
        return true;
    }
}
