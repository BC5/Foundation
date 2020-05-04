package uk.lsuth.mc.foundation.enchant;

import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.Module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EnchantModule implements Module
{
    FoundationCore core;
    NamespacedKey enchantKey;

    public EnchantModule(FoundationCore core)
    {
        this.core = core;
        enchantKey = new NamespacedKey(core,"delivery-id");
    }

    @Override
    public List<FoundationCommand> getCommands()
    {
        ArrayList<FoundationCommand> commandList = new ArrayList<FoundationCommand>();

        return commandList;
    }

    @Override
    public List<Listener> getListeners()
    {
        ArrayList<Listener> listenerList = new ArrayList<Listener>();

        return listenerList;
    }

    @Override
    public HashMap<String, Object> getTemplateData()
    {
        return new HashMap<String,Object>();
    }
}
