package uk.lsuth.mc.foundation.essentialcommands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;

public class SetMarker extends FoundationCommand
{
    FoundationCore core;

    public SetMarker(FoundationCore core)
    {
        super("setmarker");
        this.core = core;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(sender instanceof Player)
        {
            Player player = (Player) sender;
            if(!(player.hasPermission("foundation.marker")))
            {
                player.sendMessage(FoundationCore.noPermission);
                return true;
            }

            if(args.length == 4 || args.length == 5)
            {
                int[] coordinates = new int[3];
                String name;

                World w = Bukkit.getWorlds().get(0);

                if(args.length == 5)
                {
                    w = Bukkit.getWorld(args[4]);
                    if(w == null)
                    {
                        w = Bukkit.getWorlds().get(0);
                    }
                }


                try
                {
                    name = args[0];

                    coordinates[0] = Integer.parseInt(args[1]);
                    coordinates[1] = Integer.parseInt(args[2]);
                    coordinates[2] = Integer.parseInt(args[3]);
                }
                catch (NumberFormatException e)
                {
                    return false;
                }

                Location loc = new Location(w,coordinates[0],coordinates[1],coordinates[2]);

                Marker.createMarker(core.getDmgr(),core.getLmgr(),loc,player,name,w.getEnvironment() == World.Environment.NETHER);
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }
}
