package uk.lsuth.mc.foundation.gamerules;

import org.bson.Document;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.data.DataManager;
import uk.lsuth.mc.foundation.data.PlayerDataWrapper;

import java.util.Map;

public class HardcoreLivesCommand extends FoundationCommand
{
    private Map<String,String> strings;

    private DataManager dmgr;

    public HardcoreLivesCommand(FoundationCore core)
    {
        super("lives");
        this.dmgr = core.getDmgr();
        strings = core.getLmgr().getCommandStrings("lives");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args)
    {
        if(args.length == 0)
        {
            if(!(commandSender instanceof Player))
            {
                return false;
            }
            else
            {
                int lives = getLives((OfflinePlayer) commandSender,dmgr);
                if(lives == 1)
                {
                    commandSender.sendMessage(strings.get("playerlife"));
                }
                else
                {
                    commandSender.sendMessage(strings.get("playerlivesplural").replace("{x}",lives+""));
                }
                return true;
            }
        }
        else if(args.length == 1)
        {
            int lives = getLives(args[0],dmgr);
            if(lives == 1)
            {
                commandSender.sendMessage(strings.get("otherlife").replace("{x}",args[0]));
            }
            else
            {
                commandSender.sendMessage(strings.get("otherlivesplural").replace("{x}",args[0]).replace("{y}",lives+""));
            }
            return true;
        }
        return false;
    }

    public static int getLives(String p, DataManager dmgr)
    {
        PlayerDataWrapper pdw = dmgr.fetchData(p);
        Document pdoc = pdw.getPlayerDocument();

        if(pdoc.get("hardcore") == null)
        {
            return 0;
        }
        else
        {
            return ((Document) pdoc.get("hardcore")).getInteger("lives");
        }
    }

    public static int getLives(OfflinePlayer p, DataManager dmgr)
    {
        PlayerDataWrapper pdw = dmgr.fetchData(p);
        Document pdoc = pdw.getPlayerDocument();

        if(pdoc.get("hardcore") == null)
        {
            return 0;
        }
        else
        {
            return ((Document) pdoc.get("hardcore")).getInteger("lives");
        }
    }

}
