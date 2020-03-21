package uk.lsuth.mc.foundation.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;

import java.util.Map;

public class Balance extends FoundationCommand
{
    private Economy eco;
    private String balanceMessage;

    public Balance(Economy eco, Map<String,String> lang)
    {
        super("bal");
        this.eco = eco;
        this.balanceMessage = lang.get("balanceMessage");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(sender instanceof Player)
        {
            Player player = ((Player) sender);

            if(!(player.hasPermission("foundation.economy")))
            {
                sender.sendMessage(FoundationCore.noPermission);
                return true;
            }

            sender.sendMessage(balanceMessage.replaceFirst("\\{x}",eco.format(eco.getBalance(player))));
            return true;
        }
        else
        {
            return false;
        }
    }
}
