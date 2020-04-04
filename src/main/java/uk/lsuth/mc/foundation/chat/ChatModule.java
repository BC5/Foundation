package uk.lsuth.mc.foundation.chat;

import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.Module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatModule implements Module
{
    FoundationCore core;

    public ChatModule(FoundationCore core)
    {
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
    public HashMap<String, Object> getTemplateData()
    {
        return new HashMap<>();
    }
}
