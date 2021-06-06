package uk.lsuth.mc.foundation.essentialcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.data.DataManager;

public class EnderChest extends FoundationCommand
{

    DataManager dmgr;

    public EnderChest()
    {
        super("echest");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if(!sender.hasPermission("foundation.echest"))
        {
            sender.sendMessage(FoundationCore.noPermission);
            return true;
        }

        if(sender instanceof Player p)
        {
            if(args.length == 0)
            {
                p.openInventory(p.getEnderChest());
                return true;
            }
            return false;
        }
        else
        {
            return false;
        }


    }
}
