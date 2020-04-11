package uk.lsuth.mc.foundation.essentialcommands;

import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.data.PlayerDataWrapper;

import java.util.Map;

import static org.bukkit.Bukkit.getServer;

public class Teleport extends FoundationCommand
{
    Map<String,String> strings;
    FoundationCore core;

    public Teleport(FoundationCore core)
    {
        super("tp");
        this.strings = core.getLmgr().getCommandStrings("tp");
        this.core = core;
    }

    public boolean onCommand(CommandSender cmdsender, Command command, String s, String[] args)
    {
        if(!(cmdsender instanceof Player))
        {
            return false;
        }
        Player sender = (Player) cmdsender;

        boolean beaconTP = false;

        if(!(sender.hasPermission("foundation.teleport")))
        {
            if(sender.hasPermission("foundation.beaconwarp"))
            {
                Location loc = sender.getLocation();
                loc = loc.subtract(0,1,0);
                Block standingOn = sender.getWorld().getBlockAt(loc);

                if(standingOn.getType() == Material.BEACON)
                {
                    if(beaconValid(standingOn))
                    {
                        beaconTP = true;
                    }
                    else
                    {
                        sender.sendMessage(strings.get("notValidBeacon"));
                        return true;
                    }
                }
                else
                {
                    sender.sendMessage(FoundationCore.noPermission);
                    return true;
                }
            }
            else
            {
                sender.sendMessage(FoundationCore.noPermission);
                return true;
            }
        }

        switch (args.length)
        {
            //No arguments
            case 0:
                return false;

            //1 Argument. Player or Warp
            case 1:

                //Check for markers
                PlayerDataWrapper data = core.dmgr.fetchData(sender);
                Document doc = data.getPlayerDocument();
                Document markers = (Document) doc.get("markers");
                if(markers != null && markers.get(args[0]) != null)
                {
                    String[] coordsarr = ((String)markers.get(args[0])).split(",");
                    int x,y,z;
                    x = Integer.parseInt(coordsarr[0]);
                    y = Integer.parseInt(coordsarr[1]);
                    z = Integer.parseInt(coordsarr[2]);
                    Location loc = new Location(sender.getWorld(),x,y,z);
                    sender.sendMessage(strings.get("teleportToMarker").replace("{x}",args[0]));
                    sender.teleport(loc, PlayerTeleportEvent.TeleportCause.COMMAND);
                    return true;
                }
                else
                {
                    Player target = getServer().getPlayer(args[0]);

                    if(target != null)
                    {
                        sender.sendMessage(strings.get("teleportToPlayer") + target.getDisplayName());
                        sender.teleport(target, PlayerTeleportEvent.TeleportCause.COMMAND);
                    }
                    else
                    {
                        sender.sendMessage(strings.get("noSuchTeleport"));
                    }
                    return true;
                }



            //2 Arguments. X,Z
            case 2:
                double[] pos2 = new double[3];
                try
                {
                    if(args[0].charAt(0) == '~')
                    {
                        if(args[0].length() != 1)
                        {
                            pos2[0] = Double.parseDouble(args[0].substring(1));
                        }
                        pos2[0] = pos2[0] + sender.getLocation().getX();
                    }
                    else
                    {
                        pos2[0] = Double.parseDouble(args[0]);
                    }

                    if(args[1].charAt(0) == '~')
                    {
                        if(args[0].length() != 1)
                        {
                            pos2[2] = Double.parseDouble(args[1].substring(1));
                        }
                        pos2[2] = pos2[2] + sender.getLocation().getZ();
                    }
                    else
                    {
                        pos2[2] = Double.parseDouble(args[1]);
                    }

                }
                catch (NumberFormatException e)
                {
                    return false;
                }

                pos2[1] = sender.getWorld().getHighestBlockYAt((int) pos2[0],(int) pos2[2]);

                Location location2 = new Location(sender.getWorld(),pos2[0],pos2[1],pos2[2]);
                sender.sendMessage(formatCoords(pos2,strings.get("teleportToLocation")));
                //sender.sendMessage("§6Teleporting to §f[§cX:" + (int) pos2[0] + "§f] [§aY:" + (int) pos2[1] + "§f] [§bZ: " + (int) pos2[2] + "§f]");
                sender.teleport(location2, PlayerTeleportEvent.TeleportCause.COMMAND);
                break;

            //3 Arguments. X,Y,Z
            case 3:
                double[] pos = new double[3];
                try
                {
                    for(int i = 0; i <= 2; i++)
                    {
                        if(args[i].charAt(0) == '~')
                        {
                            if(args[i].length() != 1)
                            {
                                pos[i] = Double.parseDouble(args[i].substring(1));
                            }
                            switch (i)
                            {
                                case 0:
                                    pos[i] = pos[i] + sender.getLocation().getX();
                                    break;
                                case 1:
                                    pos[i] = pos[i] + sender.getLocation().getY();
                                    break;
                                case 2:
                                    pos[i] = pos[i] + sender.getLocation().getZ();
                                    break;
                            }
                        }
                        else
                        {
                            pos[i] = Double.parseDouble(args[i]);
                        }

                    }
                }
                catch (NumberFormatException e)
                {
                    return false;
                }

                Location location = new Location(sender.getWorld(),pos[0],pos[1],pos[2]);
                sender.sendMessage(formatCoords(pos,strings.get("teleportToLocation")));
                //sender.sendMessage("§6Teleporting to §f[§cX:" + (int) pos[0] + "§f] [§aY:" + (int) pos[1] + "§f] [§bZ: " + (int) pos[2] + "§f]");
                sender.teleport(location, PlayerTeleportEvent.TeleportCause.COMMAND);
                if(beaconTP)
                {
                    PotionEffect nausea = new PotionEffect(PotionEffectType.CONFUSION,100,1);
                    sender.addPotionEffect(nausea);
                    sender.playSound(sender.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE,1,0.75f);
                }
                break;

            default:
                return false;
        }
        return true;
    }

    private static String formatCoords(double[] p, String input)
    {
        int[] p2 = new int[3];
        for(int i = 0; i < 3; i++)
        {
            p2[i] = (int) p[i];
        }
        return formatCoords(p2,input);
    }

    private static String formatCoords(int[] p, String input)
    {
        return formatCoords(p[0],p[1],p[2],input);
    }

    private static String formatCoords(int x, int y, int z, String input)
    {
        input = input.replaceFirst("\\{x}",Integer.toString(x));
        input = input.replaceFirst("\\{y}",Integer.toString(y));
        input = input.replaceFirst("\\{z}",Integer.toString(z));
        return input;
    }

    private static boolean beaconValid(Block beacon)
    {
        Beacon beaconState = (Beacon) beacon.getState();

        if(beaconState.getTier() == 4)
        {
            World w = beacon.getWorld();
            Location location = beacon.getLocation();
            for(int i = 0; i < 4; i++)
            {
                Location loc0 = location.clone().subtract(1+i,1+i,1+i);
                System.out.println("i:" + i);
                for(int deltaX = 0; deltaX < 3+(2*i); deltaX++)
                {
                    System.out.println("dX:" + deltaX);
                    for(int deltaZ = 0; deltaZ < 3+(2*i); deltaZ++)
                    {
                        System.out.println("dZ:" + deltaZ);
                        if (!(w.getBlockAt(loc0.clone().add(deltaX, 0, deltaZ)).getType() == Material.DIAMOND_BLOCK))
                        {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        else
        {
            return false;
        }
    }
}
