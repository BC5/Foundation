package uk.lsuth.mc.foundation.essentialcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;

import java.util.Map;

public class Message extends FoundationCommand
{

    private Map<String,String> strings;

    public Message(FoundationCore core)
    {
        super("msg");
        strings = core.getLmgr().getCommandStrings("msg");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(args.length > 1)
        {
            Player recipient = Bukkit.getPlayer(args[0]);
            if(recipient == null)
            {
                sender.sendMessage(strings.get("noPlayer"));
                return true;
            }

            String msg = "";

            for(int i = 1; i < args.length; i++)
            {
                msg = msg + args[i] + " ";
            }

            String msg1 = strings.get("format").replace("{player}",sender.getName()).replace("{message}",msg);
            String msg2 = strings.get("inverseFormat").replace("{player}",recipient.getName()).replace("{message}",msg);

            recipient.sendMessage(msg1);
            sender.sendMessage(msg2);

            return true;
        }
        else
        {
            return false;
        }
    }
}
