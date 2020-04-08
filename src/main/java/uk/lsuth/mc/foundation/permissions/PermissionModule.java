package uk.lsuth.mc.foundation.permissions;

import org.bukkit.event.Listener;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.Module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PermissionModule implements Module
{

    FoundationCore core;

    public PermissionModule(FoundationCore core)
    {
        this.core = core;
    }

    @Override
    public List<FoundationCommand> getCommands()
    {
        return null;
    }

    @Override
    public HashMap<String, Object> getTemplateData()
    {
        return null;
    }

    @Override
    public List<Listener> getListeners()
    {
        ArrayList<Listener> listenerList = new ArrayList<Listener>();
        return listenerList;
    }
}
