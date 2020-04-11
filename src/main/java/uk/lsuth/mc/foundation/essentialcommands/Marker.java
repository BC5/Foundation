package uk.lsuth.mc.foundation.essentialcommands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.beacon.BeaconUtils;
import uk.lsuth.mc.foundation.data.PlayerDataWrapper;

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

        switch(args.length)
        {
            case 0:
                listMarkers(player);
                return true;
            case 1:
                createMarker(player,args[0]);
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

    private void createMarker(Player player,String name)
    {


        PlayerDataWrapper data = core.dmgr.fetchData(player);
        Document doc = data.getPlayerDocument();

        Document markers = (Document) doc.get("markers");
        if(markers == null)
        {
            markers = new Document();
        }

        Location loc = player.getLocation();
        markers.put(name, loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
        doc.put("markers",markers);
        player.sendMessage(core.getLmgr().getCommandStrings(this.getCommand()).get("updated"));
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

    private void listMarkers(Player player)
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
            player.sendMessage(core.getLmgr().getCommandStrings(this.getCommand()).get("markerTitle"));
            String txt = core.getLmgr().getCommandStrings(this.getCommand()).get("markerEntry");

            for(Map.Entry<String,Object> entry:markers.entrySet())
            {
                String newtxt = txt.replaceFirst("\\{a}",entry.getKey());
                String[] coords = ((String) entry.getValue()).split(",");
                newtxt = newtxt.replaceFirst("\\{x}",coords[0]);
                newtxt = newtxt.replaceFirst("\\{y}",coords[1]);
                newtxt = newtxt.replaceFirst("\\{z}",coords[2]);

                TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(newtxt));

                if(player.hasPermission("foundation.teleport") || (player.hasPermission("foundation.beaconwarp") && BeaconUtils.isStandingOnBeacon(player)))
                {
                    System.out.println("test");

                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/tp " + entry.getKey()));
                }

                player.sendMessage(textComponent);
            }
        }
    }
}
