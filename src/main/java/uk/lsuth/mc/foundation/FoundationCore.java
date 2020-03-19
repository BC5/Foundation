package uk.lsuth.mc.foundation;

import org.bson.Document;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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

    public Logger log;

    private ArrayList<Module> modules;

    @Override
    public void onEnable()
    {
        log = this.getLogger();

        log.info("Loading language en-gb");
        loadLanguage();

        log.info("Loading modules");
        modules = new ArrayList<Module>();
        modules.add(new EssentialsModule(this));
        modules.add(new Prefab());
        EconomyModule eco = new EconomyModule(lmgr);
        modules.add(eco);

        registerCommands();

        log.info("Connecting Database");
        dmgr = new MongoManager("mongodb://localhost:27017/",assembleTemplate(),log);
        eco.setDmgr(dmgr);

        log.info("Hooking listeners");
        getServer().getPluginManager().registerEvents(new PlayerListener(dmgr),this);
        getServer().getPluginManager().registerEvents(new EconomyListener(eco,lmgr.getStrings("econ")),this);
        getServer().getPluginManager().registerEvents(new ChatManager(new MessageBuilder(lmgr.getStrings("chat").get("format"))),this);
        getServer().getPluginManager().registerEvents(new RailListener(this),this);
        getServer().getPluginManager().registerEvents(new MailListener(this),this);
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
