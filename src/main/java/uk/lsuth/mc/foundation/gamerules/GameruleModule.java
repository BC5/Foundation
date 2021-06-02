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
    private ArrayList<Listener> listeners;
    private ArrayList<FoundationCommand> cmds;

    public GameruleModule(FoundationCore plugin)
    {
        listeners = new ArrayList<Listener>();
        cmds = new ArrayList<FoundationCommand>();
        Configuration cfg = plugin.getConfiguration();

        if(cfg.getBoolean("hardcore.enabled"))
        {
            hc = new Hardcore(plugin);
            listeners.add(hc);
            cmds.add(new HardcoreLivesCommand(plugin));
        }

    }

    @Override
    public List<FoundationCommand> getCommands()
    {
        return cmds;
    }

    @Override
    public List<Listener> getListeners()
    {
        return listeners;
    }

    @Override
    public HashMap<String, Object> getTemplateData()
    {
        return new HashMap<String, Object>();
    }
}
