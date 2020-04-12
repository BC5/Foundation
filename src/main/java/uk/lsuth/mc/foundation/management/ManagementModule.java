package uk.lsuth.mc.foundation.management;

import org.bukkit.event.Listener;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.Module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ManagementModule implements Module
{
    ShadowKick shadowKick;
    FoundationCore core;

    public ManagementModule(FoundationCore core)
    {
        shadowKick = new ShadowKick(core);
        this.core = core;
    }

    @Override
    public List<FoundationCommand> getCommands()
    {
        ArrayList<FoundationCommand> cmds = new ArrayList<FoundationCommand>();
        cmds.add(shadowKick);
        cmds.add(new ShadowBan(core));
        return cmds;
    }

    @Override
    public List<Listener> getListeners()
    {
        ArrayList<Listener> listeners = new ArrayList<Listener>();
        listeners.add(shadowKick.getListener());
        return listeners;
    }

    @Override
    public HashMap<String, Object> getTemplateData()
    {
        return new HashMap<String,Object>();
    }
}
