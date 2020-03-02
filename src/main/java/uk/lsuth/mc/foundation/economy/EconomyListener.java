package uk.lsuth.mc.foundation.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Map;

public class EconomyListener implements Listener
{
    Economy eco;

    String victimMessage;
    String killerMessage;

    public EconomyListener(Economy eco, Map<String,String> messages)
    {
        this.eco = eco;
        this.victimMessage = messages.get("lootVictim");
        this.killerMessage = messages.get("lootKiller");
    }

    @EventHandler
    private void onDeath(PlayerDeathEvent e)
    {
        Player victim =  e.getEntity();
        Player killer = victim.getKiller();

        if(killer != null && killer instanceof Player)
        {
            double penalty = PlayerLoot.loot(victim,killer,eco);
            victim.sendMessage(victimMessage.replaceFirst("\\{x\\}",eco.format(penalty)).replaceFirst("\\{y\\}",killer.getDisplayName()));
            killer.sendMessage(killerMessage.replaceFirst("\\{x\\}",eco.format(penalty)).replaceFirst("\\{y\\}",victim.getDisplayName()));
        }
    }
}
