package uk.lsuth.mc.foundation.wand;

import org.bukkit.event.Listener;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.Module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WandModule implements Module
{
    @Override
    public List<FoundationCommand> getCommands()
    {
        ArrayList<FoundationCommand> commands = new ArrayList<FoundationCommand>();

        return commands;
    }

    @Override
    public List<Listener> getListeners()
    {
        ArrayList<Listener> listeners = new ArrayList<Listener>();

        return listeners;
    }

    @Override
    public HashMap<String, Object> getTemplateData()
    {
        return new HashMap<String,Object>();
    }
}
