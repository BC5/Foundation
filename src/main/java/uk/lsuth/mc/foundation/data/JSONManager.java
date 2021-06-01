package uk.lsuth.mc.foundation.data;

import org.bson.Document;
import org.bukkit.OfflinePlayer;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import uk.lsuth.mc.foundation.FoundationCore;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

public class JSONManager extends DataManager
{
    boolean storeAsBSON;

    ArrayList<PlayerDataWrapper> cachedPlayers;

    Logger log;
    Document playerTemplate;

    File dataFolder;
    File playerFolder;

    public JSONManager(FoundationCore core)
    {
        dataFolder = new File(core.getDataFolder(), "data");
        playerFolder = new File(dataFolder,"player");

        throw new NotImplementedException(); //TODO:Finish
    }


    @Override
    public PlayerDataWrapper loadPlayer(OfflinePlayer player)
    {
        UUID uuid = player.getUniqueId();
        Document pdoc = loadPlayerDocumentFromJson(uuid);


        return null;
    }

    private Document loadPlayerDocumentFromJson(UUID u)
    {
        File playerFile = new File(playerFolder, u.toString()+".json");
        try
        {
            String jsonString = new String(Files.readAllBytes(playerFile.toPath()),StandardCharsets.UTF_8);
            Document pdoc = Document.parse(jsonString);
            return pdoc;
        }
        catch (IOException e)
        {
            //This *shouldn't* happen. Validation has already been done.
            log.severe("File " + playerFile + " does not exist.");
            log.severe(e.getMessage());
            return null;
        }
    }

    @Override
    public void unloadPlayer(OfflinePlayer player)
    {

    }

    @Override
    public void savePlayer(OfflinePlayer player)
    {

    }

    @Override
    public void stash()
    {

    }

    @Override
    public void setTemplate(Document template)
    {

    }

    @Override
    public boolean playerExists(OfflinePlayer player)
    {
        return false;
    }

    @Override
    public PlayerDataWrapper fetchData(OfflinePlayer player)
    {
        return null;
    }

    @Override
    public PlayerDataWrapper fetchData(String playerName)
    {
        return null;
    }
}
