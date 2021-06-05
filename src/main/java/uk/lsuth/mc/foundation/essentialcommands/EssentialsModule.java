package uk.lsuth.mc.foundation.essentialcommands;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.Listener;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.Module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EssentialsModule implements Module
{
    FoundationCore plugin;
    Economy eco;
    ArrayList<Listener> listenerList;

    public EssentialsModule(FoundationCore plugin, Economy eco)
    {
        this.plugin = plugin;
        this.eco = eco;
        listenerList = new ArrayList<Listener>();
    }

    @Override
    public List<Listener> getListeners()
    {
        listenerList.add(new MailListener(plugin));
        return listenerList;
    }

    @Override
    public List<FoundationCommand> getCommands()
    {
        List<FoundationCommand> cmds = new ArrayList<>();
        cmds.add(new Ping(plugin));
        cmds.add(new Teleport(plugin));
        cmds.add(new Marker(plugin));
        cmds.add(new Navigate(plugin));
        cmds.add(new Mail(plugin,eco));
        cmds.add(new Message(plugin));
        cmds.add(new RidePeen(plugin));
        cmds.add(new SetMarker(plugin));
        cmds.add(new Moon(plugin));
        Spectate s = new Spectate(plugin);
        cmds.add(s); listenerList.add(s);
        //cmds.add(new AFK(plugin.getLmgr().getCommandStrings("afk"))); //This doesn't work. Paper's fault
        return cmds;
    }

    @Override
    public HashMap<String, Object> getTemplateData()
    {
        return new HashMap<>();
    }
}
