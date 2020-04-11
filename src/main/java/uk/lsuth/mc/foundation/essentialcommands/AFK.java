package uk.lsuth.mc.foundation.essentialcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.lsuth.mc.foundation.FoundationCommand;

import java.util.Map;

public class AFK extends FoundationCommand
{

    Map<String,String> strings;

    public AFK(Map<String,String> strings)
    {
        super("afk");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(sender instanceof Player)
        {
            Player player = (Player) sender;

            if(player.getViewDistance() == Bukkit.getServer().getViewDistance())
            {
                player.setViewDistance(5);
                player.sendMessage(strings.get("afk"));
            }
            else
            {
                player.setViewDistance(Bukkit.getServer().getViewDistance());
                player.sendMessage(strings.get("afk-off"));
            }
            return true;
        }
        return false;
    }
}
