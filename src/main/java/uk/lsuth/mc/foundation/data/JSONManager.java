package uk.lsuth.mc.foundation.data;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import uk.lsuth.mc.foundation.FoundationCore;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;

public class JSONManager extends DataManager
{
    File dataFolder;
    File playerFolder;

    public JSONManager(FoundationCore core)
    {
        super(core.log);

        dataFolder = new File(core.getDataFolder(), "data");
        playerFolder = new File(dataFolder,"player");

        if(!playerFolder.exists())
        {
            playerFolder.mkdir();
        }
    }

    @Override
    public PlayerDataWrapper loadPlayer(OfflinePlayer player)
    {
        UUID uuid = player.getUniqueId();
        Document playerdoc;

        if(!playerExists(player))
        {
            playerdoc = createPlayerTemplate(player);
            writeJSONtoFile(playerdoc,getPlayerFile(uuid));
            PlayerDataWrapper wrapper = new PlayerDataWrapper(player,this,playerdoc,uuid);
            cachedPlayers.add(wrapper);
            return wrapper;
        }

        playerdoc = loadPlayerDocumentFromJson(uuid);

        if(playerdoc == null)
        {
            log.severe("Unable to read file. Check permissions.");
            return null;
        }
        else
        {
            //This bit is shamelessly Ctrl+C, Ctrl+V'd from MongoManager.java
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

    private void writeJSONtoFile(Document doc, File f)
    {
        try
        {
            if(!f.exists())
            {
                f.createNewFile();
            }

            FileWriter w = new FileWriter(f);
            w.write(doc.toJson());
            w.close();

        }
        catch (IOException e)
        {
            log.severe("Error writing file to disk: " + f.getName());
            log.severe(e.getMessage());
            Bukkit.broadcastMessage("§4§lFoundation has experienced a critical issue saving player data. Please see logs urgently.");
        }
    }

    private File getPlayerFile(UUID u)
    {
        return new File(playerFolder, u.toString()+".json");
    }

    private Document loadPlayerDocumentFromJson(UUID u)
    {
        File playerFile = getPlayerFile(u);
        try
        {
            String jsonString = new String(Files.readAllBytes(playerFile.toPath()),StandardCharsets.UTF_8);
            Document pdoc = Document.parse(jsonString);
            return pdoc;
        }
        catch (IOException e)
        {
            //This *shouldn't* happen. Validation has already been done.
            log.severe("File " + playerFile.getName() + " can't be read.");
            log.severe(e.getMessage());
            return null;
        }
    }

    @Override
    public void savePlayer(OfflinePlayer player)
    {
        savePlayer(fetchData(player));
    }

    @Override
    public void savePlayer(PlayerDataWrapper pdw)
    {
        writeJSONtoFile(pdw.getPlayerDocument(),getPlayerFile(pdw.getUniqueId()));
    }

    @Override
    public boolean playerExists(OfflinePlayer player)
    {
        return getPlayerFile(player.getUniqueId()).exists();
    }

    @SuppressWarnings("deprecation")
    @Override
    public PlayerDataWrapper fetchData(String playerName)
    {
        //To avoid having to mess with indexing, just using deprecated getOfflinePlayer(name)
        return fetchData(Bukkit.getOfflinePlayer(playerName));
    }
}
