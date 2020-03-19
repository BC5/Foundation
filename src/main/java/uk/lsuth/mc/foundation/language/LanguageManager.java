package uk.lsuth.mc.foundation.language;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import uk.lsuth.mc.foundation.FoundationCore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class LanguageManager
{
    FileConfiguration cfg;
    FoundationCore core;
    HashMap<String,String> lang;


    public LanguageManager(FileConfiguration cfg, FoundationCore core)
    {
        this.cfg = cfg;
        this.core = core;
        loadMCLang(core.getDataFolder().toString() + "/en_gb.json");
    }

    public String getLocalisedName(ItemStack is, boolean useItemMeta)
    {
        if(useItemMeta && is.getItemMeta().hasDisplayName())
        {
            return is.getItemMeta().getDisplayName();
        }
        else
        {
            if(lang == null)
            {
                return is.getType().name();
            }
            else
            {
                String name;
                if(is.getType().isBlock())
                {
                    name = lang.get("block.minecraft."+is.getType().name().toLowerCase());
                }
                else
                {
                    name = lang.get("item.minecraft."+is.getType().name().toLowerCase());
                }
                if(name == null || name.equals("") || name.equals(" "))
                {
                    return is.getType().name();
                }
                else
                {
                    return name;
                }
            }
        }
    }

    public void loadMCLang(String filepath)
    {
        File file = new File(filepath);
        try
        {
            JsonReader reader = new JsonReader(new FileReader(file));
            Gson gson = new Gson();
            this.lang = gson.fromJson(reader,HashMap.class);
            core.log.info("Loaded " + file.getName());
            core.log.info("Testing: STONE -> " + lang.get("block.minecraft.stone"));
        }
        catch (FileNotFoundException e)
        {
            core.log.warning("Couldn't find minecraft language file. Will not use friendly names for items.");
            //TODO: handle this
        }
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
