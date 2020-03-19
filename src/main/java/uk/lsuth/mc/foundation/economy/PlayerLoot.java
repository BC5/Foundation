package uk.lsuth.mc.foundation.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

public class PlayerLoot
{
    private static final double k = Math.pow(500d,0.001d);

    public static double loot(Player victim, Player killer, Economy eco)
    {
        double vBal = eco.getBalance(victim);
        double penalty = playerLoss(vBal);

        eco.withdrawPlayer(victim,penalty);
        eco.depositPlayer(killer,penalty);
        return penalty;
    }

    /**
     For <i>0 <= x <= 1000</i> the amount the player loses upon death increases exponentially by the equation
     <b>y=(500<sup>0.001</sup>)<sup>x</sup></b> from zero to 500.
     For <i>x > 1000</i> the amount the player loses upon death is always exactly 500.

     @param bal Player balance at time of death
     @return amount to deduct from player balance
     **/
    public static double playerLoss(double bal)
    {
        if(bal <= 1000)
        {
            return Math.pow(k,bal);
        }
        else
        {
            return 500;
        }

    }
}
