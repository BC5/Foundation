package uk.lsuth.mc.foundation;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

public abstract class FoundationCommand implements CommandExecutor
{
    public FoundationCommand(String cmd)
    {
        this.cmd = cmd;
    }

    private String cmd;
    public TabCompleter completer;

    public String getCommand()
    {
        return cmd;
    }
}
