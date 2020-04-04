package uk.lsuth.mc.foundation.chat;

import org.bson.Document;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.data.DataManager;
import uk.lsuth.mc.foundation.data.PlayerDataWrapper;

import java.util.Map;

public class Nickname extends FoundationCommand
{

    Map<String,String> strings;
    DataManager dmgr;

    public Nickname(FoundationCore core)
    {
        super("nickname");
        strings = core.getLmgr().getCommandStrings("nickname");
        dmgr = core.dmgr;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(args.length == 1)
        {
            if(sender instanceof Player)
            {


                if(args[0].length() > 16)
                {
                    return false;
                }

                Player player = (Player) sender;

                if(!(player.hasPermission("foundation.nickname")))
                {
                    sender.sendMessage(FoundationCore.noPermission);
                    return true;
                }

                PlayerDataWrapper pdw = dmgr.fetchData(player);
                Document pdoc = pdw.getPlayerDocument();

                if(args[0].equals("remove"))
                {
                    pdoc.remove("nickname");
                    player.setDisplayName(player.getName());
                }
                else
                {
                    pdoc.put("nickname",args[0]);
                    player.setDisplayName(args[0]);
                }

                player.sendMessage(strings.get("success").replace("{x}",player.getDisplayName()));
            }
            else
            {
                sender.sendMessage(strings.get("notPlayer"));
            }
            return true;
        }

        return false;

    }
}
