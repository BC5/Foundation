package uk.lsuth.mc.foundation.world;

import org.bukkit.event.Listener;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.Module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorldModule implements Module
{
    FoundationCore core;

    public WorldModule(FoundationCore core)
    {
        this.core = core;
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
        listeners.add(new BiomeChange(core));
        listeners.add(new StopEndermanGriefing());
        return listeners;
    }

    @Override
    public HashMap<String, Object> getTemplateData()
    {
        return new HashMap<String,Object>();
    }
}
