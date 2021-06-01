package uk.lsuth.mc.foundation.gamerules;

import org.bukkit.configuration.Configuration;
import org.bukkit.event.Listener;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.Module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameruleModule implements Module
{
    public Hardcore hc;

    public GameruleModule(FoundationCore plugin)
    {
        Configuration cfg = plugin.getConfiguration();

        if(cfg.getBoolean("hardcore.enabled"))
        {
            hc = new Hardcore(plugin);
        }
    }

    @Override
    public List<FoundationCommand> getCommands()
    {
        return new ArrayList<FoundationCommand>();
    }

    @Override
    public List<Listener> getListeners()
    {
        return new ArrayList<Listener>();
    }

    @Override
    public HashMap<String, Object> getTemplateData()
    {
        return new HashMap<String, Object>();
    }
}