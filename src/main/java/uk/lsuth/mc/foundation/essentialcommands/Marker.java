package uk.lsuth.mc.foundation.essentialcommands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.beacon.BeaconUtils;
import uk.lsuth.mc.foundation.data.DataManager;
import uk.lsuth.mc.foundation.data.PlayerDataWrapper;
import uk.lsuth.mc.foundation.language.LanguageManager;

import java.util.Map;

public class Marker extends FoundationCommand
{
    FoundationCore core;

    public Marker(FoundationCore core)
    {
        super("marker");
        this.core = core;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(!(sender instanceof Player))
        {
            return false;
        }
        Player player = (Player) sender;

        if(!(player.hasPermission("foundation.marker")))
        {
            player.sendMessage(FoundationCore.noPermission);
            return true;
        }

        boolean nether = false;
        if(player.getWorld().getEnvironment() == World.Environment.NETHER)
        {
            nether = true;
        }

        switch(args.length)
        {
            case 0:
                listMarkers(player,nether);
                return true;
            case 1:
                createMarker(player,args[0],nether);
                return true;
            case 2:
                if(args[1].equalsIgnoreCase("remove"))
                {
                    removeMarker(player,args[0]);
                    return true;
                }
                else
                {
                    return false;
                }
            default:
                return false;
        }
    }

    public static void createMarker(DataManager dmgr, LanguageManager lmgr, Location loc, Player player, String name, boolean nether)
    {
        PlayerDataWrapper data = dmgr.fetchData(player);
        Document doc = data.getPlayerDocument();

        Document markers = (Document) doc.get("markers");
        if(markers == null)
        {
            markers = new Document();
        }

        if(nether)
        {
            markers.put(name, loc.getBlockX()*8 + "," + loc.getBlockY() + "," + loc.getBlockZ()*8);
        }
        else
        {
            markers.put(name, loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
        }

        doc.put("markers",markers);
        player.sendMessage(lmgr.getCommandStrings("marker").get("updated"));
    }

    private void createMarker(Location location, Player player, String name, boolean nether)
    {
        createMarker(core.getDmgr(),core.getLmgr(),location,player,name,nether);
    }

    private void createMarker(Player player,String name,boolean nether)
    {
        createMarker(player.getLocation(),player,name,nether);
    }

    private void removeMarker(Player player,String name)
    {
        PlayerDataWrapper data = core.dmgr.fetchData(player);
        Document doc = data.getPlayerDocument();

        Document markers = (Document) doc.get("markers");
        if(markers == null)
        {
            markers = new Document();
        }
        if(doc.containsKey(name))
        {
            doc.remove(name);
        }
        player.sendMessage(core.getLmgr().getCommandStrings(this.getCommand()).get("removed"));
    }

    private void listMarkers(Player player, boolean nether)
    {
        PlayerDataWrapper data = core.dmgr.fetchData(player);
        Document doc = data.getPlayerDocument();

        Document markers = (Document) doc.get("markers");
        if(markers == null)
        {
            player.sendMessage(core.getLmgr().getCommandStrings(this.getCommand()).get("noMarkers"));
        }
        else
        {
            if(nether)
            {
                player.sendMessage(core.getLmgr().getCommandStrings(this.getCommand()).get("markerTitleNether"));
            }
            else
            {
                player.sendMessage(core.getLmgr().getCommandStrings(this.getCommand()).get("markerTitle"));
            }

            String txt = core.getLmgr().getCommandStrings(this.getCommand()).get("markerEntry");

            for(Map.Entry<String,Object> entry:markers.entrySet())
            {
                String newtxt = txt.replaceFirst("\\{a}",entry.getKey());
                String[] coords = ((String) entry.getValue()).split(",");

                if (nether)
                {
                    int[] netherCoords = new int[3];
                    netherCoords[0] = Integer.parseInt(coords[0]) / 8;
                    netherCoords[1] = Integer.parseInt(coords[1]);
                    netherCoords[2] = Integer.parseInt(coords[2]) / 8;

                    newtxt = newtxt.replaceFirst("\\{x}",Integer.toString(netherCoords[0]));
                    newtxt = newtxt.replaceFirst("\\{y}",Integer.toString(netherCoords[1]));
                    newtxt = newtxt.replaceFirst("\\{z}",Integer.toString(netherCoords[2]));
                }
                else
                {
                    newtxt = newtxt.replaceFirst("\\{x}",coords[0]);
                    newtxt = newtxt.replaceFirst("\\{y}",coords[1]);
                    newtxt = newtxt.replaceFirst("\\{z}",coords[2]);
                }


                TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(newtxt));

                if(player.hasPermission("foundation.teleport") || (player.hasPermission("foundation.beacon.warp") && BeaconUtils.isStandingOnBeacon(player)))
                {
                    System.out.println("test");

                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/tp " + entry.getKey()));
                }

                player.sendMessage(textComponent);
            }
        }
    }
}
