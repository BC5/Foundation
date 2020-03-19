package uk.lsuth.mc.foundation.essentialcommands;

import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.data.PlayerDataWrapper;

public class Navigate extends FoundationCommand
{
    FoundationCore core;

    public Navigate(FoundationCore core)
    {
        super("navigate");
        this.core = core;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(args.length != 1)
        {
            if(sender instanceof Player)
            {
                ((Player) sender).setCompassTarget(((Player) sender).getWorld().getSpawnLocation());
                sender.sendMessage(core.getLmgr().getCommandStrings("navigate").get("navReset"));
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            if(sender instanceof Player)
            {
                Player player = (Player) sender;

                PlayerDataWrapper data = core.dmgr.fetchData(player);
                Document doc = data.getPlayerDocument();
                Document markers = (Document) doc.get("markers");
                String coords = (String) markers.get(args[0]);
                if(coords == null)
                {
                    return false;
                }
                else
                {
                    String[] coordsarr = coords.split(",");
                    int x,y,z;
                    x = Integer.parseInt(coordsarr[0]);
                    y = Integer.parseInt(coordsarr[1]);
                    z = Integer.parseInt(coordsarr[2]);
                    Location loc = new Location(player.getWorld(),x,y,z);
                    player.setCompassTarget(loc);
                    sender.sendMessage(core.getLmgr().getCommandStrings("navigate").get("navSet").replaceFirst("\\{x}",args[0]));
                    return true;
                }

            }
            else
            {
                return false;
            }

        }
    }
}
