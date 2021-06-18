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
    Slabber slabber;
    Bind bind;

    public WorldModule(FoundationCore core)
    {
        this.core = core;
        slabber = new Slabber(core);
        bind = new Bind(core);
    }

    @Override
    public List<FoundationCommand> getCommands()
    {
        ArrayList<FoundationCommand> cmds = new ArrayList<FoundationCommand>();
        cmds.add(slabber);
        cmds.add(bind);
        cmds.add(new ContainerQuery(core));
        return cmds;
    }

    @Override
    public List<Listener> getListeners()
    {
        ArrayList<Listener> listeners = new ArrayList<Listener>();
        listeners.add(new BiomeChange(core));
        listeners.add(new StopEndermanGriefing());
        listeners.add(slabber);
        listeners.add(bind);
        listeners.add(new FurnacePersistence(core));
        return listeners;
    }

    @Override
    public HashMap<String, Object> getTemplateData()
    {
        return new HashMap<String,Object>();
    }
}
