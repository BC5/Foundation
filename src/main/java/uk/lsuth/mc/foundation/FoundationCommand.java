package uk.lsuth.mc.foundation;

import org.bukkit.command.CommandExecutor;

public abstract class FoundationCommand implements CommandExecutor
{
    public FoundationCommand(String cmd)
    {
        this.cmd = cmd;
    }

    private String cmd;

    public String getCommand()
    {
        return cmd;
    }
}
