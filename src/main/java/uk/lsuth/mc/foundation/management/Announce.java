package uk.lsuth.mc.foundation.management;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import uk.lsuth.mc.foundation.FoundationCommand;

import java.util.Map;

public class Announce extends FoundationCommand
{
    Map<String,String> strings;

    public Announce(Map<String,String> strings)
    {
        super("announce");
        this.strings = strings;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args)
    {
        if(args.length == 0)
        {
            return false;
        }
        if(sender.hasPermission("foundation.management.announce"))
        {
            String msg = "";

            for(int i = 0; i < args.length; i++)
            {
                msg = msg + args[i] + " ";
            }

            String msg1 = strings.get("format").replace("{message}",msg);

            Bukkit.getServer().broadcastMessage(msg1);
            return true;
        }
        else
        {
            return false;
        }
    }
}
