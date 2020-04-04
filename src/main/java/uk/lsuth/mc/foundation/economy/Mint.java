package uk.lsuth.mc.foundation.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;

import java.util.Map;

public class Mint extends FoundationCommand
{

    private Economy eco;

    private String mintSuccess;
    private String mintFailure;

    public Mint(Economy eco, Map<String,String> lang)
    {
        super("mint");
        this.eco = eco;
        this.mintSuccess = lang.get("mintMessage");
        this.mintFailure = lang.get("mintFailure");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings)
    {
        if(commandSender instanceof Player)
        {
            Player player = (Player) commandSender;

            if(!(player.hasPermission("foundation.economy.mint")))
            {
                player.sendMessage(FoundationCore.noPermission);
                return true;
            }

            Inventory inv = player.getInventory();

            int i;
            if(strings.length == 0)
            {
                i = 1;
            }
            else
            {
                i = Integer.parseInt(strings[0]);
            }

            int delta = 0;
            for (;i > 0; i--)
            {
                if(inv.contains(Material.DIAMOND))
                {
                    ItemStack x = inv.getItem(inv.first(Material.DIAMOND));
                    x.setAmount(x.getAmount()-1);
                    delta = delta + 100;

                }
                else
                {
                    break;
                }
            }

            if(delta == 0)
            {
                commandSender.sendMessage(mintFailure);
            }
            else
            {
                eco.depositPlayer(player,delta);
                commandSender.sendMessage(mintSuccess.replaceFirst("\\{x}",eco.format(delta)));
            }


            return true;
        }
        else
        {
            commandSender.sendMessage("You must be a player");
            return false;
        }

    }
}
