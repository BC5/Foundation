package uk.lsuth.mc.foundation.pvp;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.Module;
import uk.lsuth.mc.foundation.language.LanguageManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PVPModule implements Module
{
    LanguageManager lmgr;
    FileConfiguration config;


    public PVPModule(FoundationCore core)
    {
        lmgr = core.getLmgr();
        config = core.getConfiguration();
    }

    @Override
    public List<FoundationCommand> getCommands()
    {
        return new ArrayList<FoundationCommand>();
    }

    @Override
    public List<Listener> getListeners()
    {
        ArrayList<Listener> listeners = new ArrayList<Listener>();
        listeners.add(new PVPListener(lmgr.getStrings("pvp"),config));
        return listeners;
    }

    @Override
    public HashMap<String, Object> getTemplateData()
    {
        return new HashMap<String,Object>();
    }
}
