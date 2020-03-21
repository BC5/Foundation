package uk.lsuth.mc.foundation.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.data.DataManager;
import uk.lsuth.mc.foundation.data.PlayerDataWrapper;

import java.util.Map;

public class Transfer extends FoundationCommand
{
    Economy eco;
    DataManager dmgr;
    Map<String,String> strings;

    public Transfer(Economy eco, Map<String,String> strings, DataManager dmgr)
    {
        super("transfer");
        this.eco = eco;
        this.dmgr = dmgr;
        this.strings = strings;
    }

    @Override
    public boolean onCommand(CommandSender sendr, Command command, String label, String[] args)
    {
        if(!(sendr instanceof Player))
        {
            return false;
        }

        Player sender = (Player) sendr;

        if(args.length == 2)
        {
            if(!(sender.hasPermission("foundation.economy.transfer")))
            {
                sender.sendMessage(FoundationCore.noPermission);
                return true;
            }

            OfflinePlayer recipient;
            recipient = Bukkit.getServer().getPlayer(args[0]);
            if(recipient == null)
            {
                PlayerDataWrapper dataWrapper = dmgr.fetchData(args[0]);
                if(dataWrapper == null)
                {
                    sender.sendMessage(strings.get("noPlayer"));
                    return true;
                }
                recipient = dataWrapper.getPlayer();
            }

            double amount = 0;
            try
            {
                amount = EconomyModule.moneyParse(args[1]);
            }
            catch (NumberFormatException e)
            {
                return false;
            }

            if(amount <= 0)
            {
                sender.sendMessage(strings.get("largerThanZero"));
                return true;
            }

            if(eco.has(sender,amount))
            {
                if(eco.withdrawPlayer(sender,amount).transactionSuccess())
                {
                    if(eco.depositPlayer(recipient,amount).transactionSuccess())
                    {
                        sender.sendMessage(strings.get("transactionSuccess").replace("{x}",eco.format(amount)).replace("{y}",recipient.getName()));
                        return true;
                    }
                    else
                    {
                        eco.depositPlayer(sender,amount);
                        sender.sendMessage(strings.get("transactionFailed"));
                        return true;
                    }
                }
                else
                {
                    sender.sendMessage(strings.get("transactionFailed"));
                    return true;
                }

            }
            else
            {
                sender.sendMessage(strings.get("insufficientFunds"));
                return true;
            }




        }
        return false;
    }
}
