package uk.lsuth.mc.foundation.wand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import uk.lsuth.mc.foundation.FoundationCommand;

public class WandCreator extends FoundationCommand
{
    public WandCreator()
    {
        super("wand");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        return false;
    }
}
