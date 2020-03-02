package uk.lsuth.mc.foundation.language;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import uk.lsuth.mc.foundation.FoundationCore;

import java.io.File;
import java.util.Map;

public class LanguageManager
{
    FileConfiguration cfg;
    FoundationCore core;

    public LanguageManager(FileConfiguration cfg, FoundationCore core)
    {
        this.cfg = cfg;
        this.core = core;
    }

    public Map<String,String> getCommandStrings(String command)
    {
        return getStrings("cmds."+command);
    }

    public Map<String,String> getStrings(String path)
    {
        Map<String,Object> x = cfg.getConfigurationSection(path).getValues(false);
        try
        {
            @SuppressWarnings("unchecked")
            Map<String,String> y = (Map) x;
            return y;
        }
        catch (ClassCastException e)
        {
            core.log.severe("Malformed language file. Foundation cannot continue");
            Bukkit.getPluginManager().disablePlugin(core);
            return null;
        }
    }
}
