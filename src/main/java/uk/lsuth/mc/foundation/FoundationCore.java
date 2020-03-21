package uk.lsuth.mc.foundation;

import net.milkbowl.vault.economy.Economy;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import uk.lsuth.mc.foundation.chat.ChatManager;
import uk.lsuth.mc.foundation.chat.MessageBuilder;
import uk.lsuth.mc.foundation.data.DataManager;
import uk.lsuth.mc.foundation.data.MongoManager;
import uk.lsuth.mc.foundation.data.PlayerListener;
import uk.lsuth.mc.foundation.economy.EconomyListener;
import uk.lsuth.mc.foundation.economy.EconomyModule;
import uk.lsuth.mc.foundation.essentialcommands.EssentialsModule;
import uk.lsuth.mc.foundation.essentialcommands.MailListener;
import uk.lsuth.mc.foundation.language.LanguageManager;
import uk.lsuth.mc.foundation.railroute.RailListener;
import uk.lsuth.mc.foundation.structure.Prefab;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

public class FoundationCore extends JavaPlugin
{
    private FileConfiguration cfg = getConfig();
    private LanguageManager lmgr;
    public DataManager dmgr;

    public static String noPermission;

    public Logger log;

    private ArrayList<Module> modules;

    @Override
    public void onEnable()
    {
        log = this.getLogger();

        log.info("Loading language en-gb");
        loadLanguage();

        noPermission = lmgr.getStrings("perm").get("noPermission");

        log.info("Connecting Database");
        dmgr = new MongoManager("mongodb://localhost:27017/",log);

        log.info("Loading modules");
        modules = new ArrayList<Module>();
        modules.add(new EssentialsModule(this));
        modules.add(new Prefab());
        EconomyModule eco = new EconomyModule(lmgr,dmgr);
        modules.add(eco);

        dmgr.setTemplate(assembleTemplate());

        registerCommands();

        PluginManager pluginManager = getServer().getPluginManager();

        log.info("Hooking listeners");
        pluginManager.registerEvents(new PlayerListener(dmgr),this);
        pluginManager.registerEvents(new EconomyListener(eco,lmgr.getStrings("econ")),this);
        pluginManager.registerEvents(new ChatManager(new MessageBuilder(lmgr.getStrings("chat").get("format"))),this);
        pluginManager.registerEvents(new RailListener(this),this);
        pluginManager.registerEvents(new MailListener(this),this);

        log.info("Registering Economy");
        Plugin vault = pluginManager.getPlugin("Vault");
        Bukkit.getServicesManager().register(Economy.class,eco,vault, ServicePriority.High);
    }

    private void loadLanguage()
    {
        File langcfgfile = new File(getDataFolder(),"en-gb.yml");
        if(!langcfgfile.exists())
        {
            saveResource(langcfgfile.getName(), false);
        }
        FileConfiguration langcfg = YamlConfiguration.loadConfiguration(langcfgfile);
        lmgr = new LanguageManager(langcfg,this);
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
            }
        }
    }
}
