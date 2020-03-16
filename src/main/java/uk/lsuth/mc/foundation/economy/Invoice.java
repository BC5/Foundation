package uk.lsuth.mc.foundation.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.lsuth.mc.foundation.FoundationCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Invoice extends FoundationCommand
{
    private HashMap<UUID,PlayerInvoice> latestInvoices;
    private Economy eco;
    private Map<String,String> lang;

    public Invoice(Economy eco, Map<String,String> lang)
    {
        super("invoice");
        this.eco = eco;
        this.lang = lang;
        latestInvoices = new HashMap<UUID,PlayerInvoice>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        //invoice send <player> <amount>
        //invoice pay

        if(sender instanceof Player)
        {
            Player player = (Player) sender;
            if (args.length == 3)
            {
                if (!args[0].equals("send")) return false;
                Player dest = Bukkit.getServer().getPlayer(args[1]);
                if (dest == null)
                {
                    sender.sendMessage(lang.get("none"));
                }
                else
                {
                    double amnt;
                    try
                    {
                         amnt = Double.parseDouble(args[2]);
                    }
                    catch (NumberFormatException e)
                    {
                        return false;
                    }
                    PlayerInvoice inv = new PlayerInvoice(player, dest, amnt);
                    latestInvoices.put(dest.getUniqueId(),inv);
                    sender.sendMessage(lang.get("sent").replace("{x}",dest.getDisplayName()));
                    dest.sendMessage(lang.get("received").replace("{x}",eco.format(amnt)).replace("{y}",player.getDisplayName()));
                    return true;
                }
            }
            else if (args.length == 1)
            {
                if (!args[0].equals("pay")) return false;
                PlayerInvoice inv = latestInvoices.get(player.getUniqueId());
                if(inv == null)
                {
                    player.sendMessage(lang.get("none"));
                    return true;
                }
                else
                {
                    if(inv.payInvoice(eco))
                    {
                        player.sendMessage(lang.get("paid").replace("{x}",eco.format(inv.amount)).replace("{y}",inv.sender.getName()));
                        latestInvoices.remove(player.getUniqueId());
                        return true;
                    }
                    else
                    {
                        player.sendMessage(lang.get("insufficientFunds"));
                        return true;
                    }
                }


            }
        }

        return false;
    }

    static class PlayerInvoice
    {
        private OfflinePlayer recipient;
        public OfflinePlayer sender;
        public double amount;

        public PlayerInvoice(OfflinePlayer sender, OfflinePlayer recipient,double amount)
        {
            this.sender = sender;
            this.recipient = recipient;
            this.amount = amount;
        }

        public boolean payInvoice(Economy eco)
        {
            if(eco.has(recipient,amount))
            {
                eco.withdrawPlayer(recipient,amount);
                eco.depositPlayer(sender,amount);
                return true;
            }
            else
            {
                return false;
            }
        }
    }
}
