package uk.lsuth.mc.foundation.essentialcommands;

import org.bukkit.plugin.java.JavaPlugin;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.Module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EssentialsModule implements Module
{
    FoundationCore plugin;

    public EssentialsModule(FoundationCore plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public List<FoundationCommand> getCommands()
    {
        List<FoundationCommand> cmds = new ArrayList<>();
        cmds.add(new Ping(plugin));
        cmds.add(new Teleport(plugin));
        cmds.add(new Marker(plugin));
        cmds.add(new Navigate(plugin));
        return cmds;
    }

    @Override
    public HashMap<String, Object> getTemplateData()
    {
        return new HashMap<>();
    }
}
