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
import uk.lsuth.mc.foundation.chat.ChatModule;
import uk.lsuth.mc.foundation.data.DataManager;
import uk.lsuth.mc.foundation.data.MongoManager;
import uk.lsuth.mc.foundation.data.PlayerListener;
import uk.lsuth.mc.foundation.economy.EconomyModule;
import uk.lsuth.mc.foundation.essentialcommands.EssentialsModule;
import uk.lsuth.mc.foundation.language.LanguageManager;
import uk.lsuth.mc.foundation.pvp.PVPModule;
import uk.lsuth.mc.foundation.railroute.RailListener;
import uk.lsuth.mc.foundation.structure.Prefab;

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

    @Override
    public void onEnable()
    {
        log = this.getLogger();

        log.info("Loading configuration");
        cfg = this.getConfig();

        log.info("Loading language " + cfg.getString("lang"));
        loadLanguage(cfg.getString("lang"));

        noPermission = lmgr.getStrings("perm").get("noPermission");

        log.info("Connecting Database");
        dmgr = new MongoManager("mongodb://localhost:27017/",log);

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

        dmgr.setTemplate(assembleTemplate());

        registerCommands();



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


    private void registerCommands()
    {
        for(Module md:modules)
        {
            for(FoundationCommand cmd:md.getCommands())
            {
                log.info("Registering command /" + cmd.getCommand());
                this.getCommand(cmd.getCommand()).setExecutor(cmd);
                if(cmd.completer != null)
                {
                    System.out.println("not null!");
                    this.getCommand(cmd.getCommand()).setTabCompleter(cmd.completer);
                }
            }
        }
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
