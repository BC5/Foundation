package uk.lsuth.mc.foundation.chat;

import org.bukkit.event.Listener;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.Module;
import uk.lsuth.mc.foundation.chat.legacy.ChatManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatModule implements Module
{
    FoundationCore core;
    ArrayList<Listener> listenerList;



    public ChatModule(FoundationCore core)
    {
        listenerList = new ArrayList<Listener>();

        if(core.paperAPI)
        {
            listenerList.add(new PaperChatManager(core));
        }
        else
        {
            core.log.warning("Using legacy chat system");
            listenerList.add(new ChatManager(core));
        }

        this.core = core;
    }

    @Override
    public List<FoundationCommand> getCommands()
    {
        ArrayList<FoundationCommand> cmds = new ArrayList<FoundationCommand>();
        cmds.add(new Nickname(core));
        return cmds;
    }

    @Override
    public List<Listener> getListeners()
    {
        return listenerList;
    }

    @Override
    public HashMap<String, Object> getTemplateData()
    {
        return new HashMap<>();
    }
}
