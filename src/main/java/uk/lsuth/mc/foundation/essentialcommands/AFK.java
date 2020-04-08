package uk.lsuth.mc.foundation.essentialcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.lsuth.mc.foundation.FoundationCommand;

public class AFK extends FoundationCommand
{
    public AFK(String cmd)
    {
        super("afk");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(sender instanceof Player)
        {
            Player player = (Player) sender;
        }
    }
}
