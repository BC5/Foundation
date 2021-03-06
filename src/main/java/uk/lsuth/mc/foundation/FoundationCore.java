package uk.lsuth.mc.foundation;

import net.milkbowl.vault.economy.Economy;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import uk.lsuth.mc.foundation.beacon.BeaconUtils;
import uk.lsuth.mc.foundation.chat.ChatModule;
import uk.lsuth.mc.foundation.data.DataManager;
import uk.lsuth.mc.foundation.data.JSONManager;
import uk.lsuth.mc.foundation.data.MongoManager;
import uk.lsuth.mc.foundation.data.PlayerListener;
import uk.lsuth.mc.foundation.economy.EconomyModule;
import uk.lsuth.mc.foundation.essentialcommands.EssentialsModule;
import uk.lsuth.mc.foundation.fabric.FabricModule;
import uk.lsuth.mc.foundation.gamerules.GameruleModule;
import uk.lsuth.mc.foundation.language.ItemSearch;
import uk.lsuth.mc.foundation.language.LanguageManager;
import uk.lsuth.mc.foundation.management.ManagementModule;
import uk.lsuth.mc.foundation.pvp.PVPModule;
import uk.lsuth.mc.foundation.railroute.Junction;
import uk.lsuth.mc.foundation.railroute.RailListener;
import uk.lsuth.mc.foundation.structure.Prefab;
import uk.lsuth.mc.foundation.world.WorldModule;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

public class FoundationCore extends JavaPlugin
{
    private FileConfiguration cfg;
    private LanguageManager lmgr;
    public DataManager dmgr;

    public static String noPermission;

    public Logger log;

    private ArrayList<Module> modules;

    public boolean paperAPI;
    public boolean spigotAPI;


    @Override
    public void onEnable()
    {
        log = this.getLogger();

        try
        {
            Class.forName("org.spigotmc.SpigotConfig");
            spigotAPI = true;
        }
        catch (ClassNotFoundException e)
        {
            spigotAPI = false;
        }

        try
        {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            paperAPI = true;
        }
        catch (ClassNotFoundException e)
        {
            paperAPI = false;
        }

        if(!spigotAPI) log.severe("Did not detect Spigot APIs present. Expect errors");
        if(!paperAPI) log.warning("Did not detect Paper APIs present. Expect some features to be disabled");


        log.info("Loading configuration");
        cfg = this.getConfig();

        log.info("Loading language " + cfg.getString("lang"));
        loadLanguage(cfg.getString("lang"));

        noPermission = lmgr.getStrings("perm").get("noPermission");

        if(cfg.getBoolean("data.flatfile"))
        {
            log.info("Using JSON Data Storage");
            dmgr = new JSONManager(this);
        }
        else
        {
            log.info("Using MongoDB");
            dmgr = new MongoManager(cfg.getString("data.mongourl"),log);
        }

        log.info("Loading modules");
        modules = new ArrayList<Module>();

        EconomyModule eco = new EconomyModule(this);
        modules.add(eco);

        log.info("Registering Economy");
        PluginManager pluginManager = getServer().getPluginManager();
        Plugin vault = pluginManager.getPlugin("Vault");
        Bukkit.getServicesManager().register(Economy.class,eco,vault, ServicePriority.High);

        modules.add(new EssentialsModule(this,eco));
        modules.add(new Prefab());
        modules.add(new ChatModule(this));
        modules.add(new PVPModule(this));
        modules.add(new BeaconUtils(this));
        modules.add(new ManagementModule(this));
        modules.add(new WorldModule(this));
        modules.add(new FabricModule(this));
        modules.add(new GameruleModule(this));

        dmgr.setTemplate(assembleTemplate());

        registerCommands();

        new Junction(this);

        log.info("Hooking listeners");
        //Module listeners
        registerModules(pluginManager);

        //Other listeners
        pluginManager.registerEvents(new PlayerListener(this),this);
        pluginManager.registerEvents(new RailListener(this),this);


    }

    public FileConfiguration getConfiguration()
    {
        return cfg;
    }

    public DataManager getDmgr()
    {
        return dmgr;
    }

    private void loadLanguage(String lang)
    {
        File langcfgfile = new File(getDataFolder(),lang + ".yml");
        if(!langcfgfile.exists())
        {
            saveResource(langcfgfile.getName(), false);
        }
        FileConfiguration langcfg = YamlConfiguration.loadConfiguration(langcfgfile);
        lmgr = new LanguageManager(langcfg,this);
        log.info("Loaded " + langcfgfile.getName());
    }

    private Document assembleTemplate()
    {
        Document doc = new Document();
        for(Module md:modules)
        {
            for(Map.Entry x:md.getTemplateData().entrySet())
            {
                doc.append(x.getKey().toString(),x.getValue());
            }
        }
        return doc;
    }

    public LanguageManager getLmgr()
    {
        return lmgr;
    }

    @Override
    public void onDisable()
    {
        //Save all cached players to disk
        dmgr.stash();
    }

    public ItemSearch getItemSearch()
    {
        return lmgr.getItemSearch();
    }

    private void registerCommands()
    {
        int cmds = 0;
        for(Module md:modules)
        {
            for(FoundationCommand cmd:md.getCommands())
            {
                try
                {
                    log.fine("Registering command /" + cmd.getCommand());
                    this.getCommand(cmd.getCommand()).setExecutor(cmd);
                    if (cmd.completer != null)
                    {
                        this.getCommand(cmd.getCommand()).setTabCompleter(cmd.completer);
                    }
                    cmds++;
                }
                catch (Exception e)
                {
                    log.severe("Failed to register command");
                    e.printStackTrace();
                }
            }
        }
        log.info("Registered " + cmds + " commands");
    }

    private void registerModules(PluginManager pluginManager)
    {
        for(Module md:modules)
        {
            for(Listener listener:md.getListeners())
            {
                pluginManager.registerEvents(listener,this);
            }
        }
    }
}
