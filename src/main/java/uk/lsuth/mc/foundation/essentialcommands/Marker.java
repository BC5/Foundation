package uk.lsuth.mc.foundation.essentialcommands;

import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.data.DataManager;
import uk.lsuth.mc.foundation.data.PlayerDataWrapper;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        switch(args.length)
        {
            case 0:
                listMarkers(player);
                return true;
            case 1:
                createMarker(player,args[0]);
                return true;
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
                String newtxt = txt.replaceFirst("\\{a\\}",entry.getKey());
                String[] coords = ((String) entry.getValue()).split(",");
                newtxt = newtxt.replaceFirst("\\{x\\}",coords[0]);
                newtxt = newtxt.replaceFirst("\\{y\\}",coords[1]);
                newtxt = newtxt.replaceFirst("\\{z\\}",coords[2]);
                player.sendMessage(newtxt);
            }
        }
    }
}
