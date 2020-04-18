package uk.lsuth.mc.foundation.management;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;

import java.util.Map;

public class Where extends FoundationCommand
{
    Map<String,String> strings;

    public Where(FoundationCore core)
    {
        super("where");
        strings = core.getLmgr().getCommandStrings("where");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(!(sender.hasPermission("foundation.management.where")))
        {
            sender.sendMessage(FoundationCore.noPermission);
            return true;
        }

        if(args.length == 2)
        {
            switch (args[0])
            {
                case "is":
                    Player p = Bukkit.getPlayer(args[1]);
                    if(p == null)
                    {
                        sender.sendMessage(strings.get("noPlayer"));
                        return true;
                    }
                    sendLocation(sender,p);
                    return true;
                case "am":
                    if(args[1].equalsIgnoreCase("I"))
                    {
                        if(sender instanceof Player)
                        {
                            sendLocation(sender,(Player) sender);
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
                default:
                    return false;
            }
        }
        else
        {
            return false;
        }
    }

    private void sendLocation(CommandSender locator, Player locatee)
    {
        World w = locatee.getWorld();
        Location loc = locatee.getLocation();
        Chunk c = loc.getChunk();

        // Calculation for region file coordinates
        // https://minecraft.gamepedia.com/Region_file_format
        int regionX = (int) Math.floor(c.getX() / 32.0);
        int regionZ = (int) Math.floor(c.getZ() / 32.0);

        String msg;
        if(locator.equals(locatee))
        {
            msg = strings.get("are");
        }
        else
        {
            msg = strings.get("is");
            msg = msg.replace("{player}",locatee.getDisplayName());
        }

        msg = msg.replace("{x}",Integer.toString(loc.getBlockX()));
        msg = msg.replace("{y}",Integer.toString(loc.getBlockY()));
        msg = msg.replace("{z}",Integer.toString(loc.getBlockZ()));
        msg = msg.replace("{world}",w.getName());

        String hover = strings.get("hover");
        hover = hover.replace("{rx}",Integer.toString(regionX));
        hover = hover.replace("{rz}",Integer.toString(regionZ));
        hover = hover.replace("{cx}",Integer.toString(c.getX()));
        hover = hover.replace("{cz}",Integer.toString(c.getZ()));

        TextComponent message = new TextComponent(TextComponent.fromLegacyText(msg));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,TextComponent.fromLegacyText(hover)));

        locator.sendMessage(message);
    }
}
