package uk.lsuth.mc.foundation.essentialcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;

import java.util.Map;

public class RidePeen extends FoundationCommand
{

    Map<String,String> strings;

    public RidePeen(FoundationCore core)
    {
        super("ride");
        strings = core.getLmgr().getCommandStrings("ride");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(sender instanceof Player)
        {
            Player player = (Player) sender;

            Entity entity;

            try
            {
                 entity = player.getNearbyEntities(5,5,5).get(0);
            }
            catch (IndexOutOfBoundsException e)
            {
                player.sendMessage(strings.get("nothing"));
                return false;
            }


            if(entity != null)
            {
                if(entity instanceof Player)
                {
                    if(((Player) entity).getUniqueId().toString().equals("4eea796e-8a06-45cf-aafe-052e7706d4e3"))
                    {
                        entity.addPassenger(player);
                        player.sendMessage(strings.get("Peen"));
                        return true;
                    }
                }
                player.sendMessage(strings.get("notPeen"));
            }
            else
            {
                player.sendMessage(strings.get("nothing"));
            }
            return false;
        }
        else
        {
            return false;
        }
    }
}
