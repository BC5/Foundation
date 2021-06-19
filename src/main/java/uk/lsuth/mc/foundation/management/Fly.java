package uk.lsuth.mc.foundation.management;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;

import java.util.Map;

public class Fly extends FoundationCommand
{
    Map<String,String> strings;

    public Fly(FoundationCore core)
    {
        super("fly");
        strings = core.getLmgr().getCommandStrings("fly");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if(!sender.hasPermission("foundation.fly"))
        {
            sender.sendMessage(FoundationCore.noPermission);
            return true;
        }

        if(sender instanceof Player p)
        {
            boolean flight = p.getAllowFlight();
            flight = !flight;
            p.setAllowFlight(flight);

            if(flight)
            {
                p.sendMessage(strings.get("flyon"));
            }
            else
            {
                p.sendMessage(strings.get("flyoff"));
            }
            return true;
        }
        return false;
    }
}
