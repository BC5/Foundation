package uk.lsuth.mc.foundation.beacon;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LevitationBeacon implements Listener
{
    @EventHandler
    public void onMove(PlayerMoveEvent e)
    {
        World w = e.getTo().getWorld();
        Block highest = w.getHighestBlockAt(e.getTo());

        if(highest.getType() == Material.GLASS)
        {
            while(true)
            {
                highest = highest.getRelative(0,-1,0);

                if(highest == null)
                {
                    break;
                }

                if(highest.getType() == Material.GLASS)
                {
                    //Do nothing
                }
                else if(highest.getType() == Material.AIR)
                {
                    //Do nothing
                }
                else if(highest.getType() == Material.BEACON)
                {
                    if(BeaconUtils.beaconIsTier(highest,1,Material.EMERALD_BLOCK))
                    {
                        applyLevitation(e.getPlayer(),BeaconUtils.getBeaconTier(highest)*2);
                    }
                    else
                    {
                        break;
                    }
                }
                else
                {
                    break;
                }
            }
        }
        else if(highest.getType() == Material.BEACON)
        {
            if(BeaconUtils.beaconIsTier(highest,1,Material.EMERALD_BLOCK))
            {
                applyLevitation(e.getPlayer(),BeaconUtils.getBeaconTier(highest)*2);
            }
            else
            {
                return;
            }
        }
        else
        {
            return;
        }

    }

    private void applyLevitation(Player p, int level)
    {
        PotionEffect effect = new PotionEffect(PotionEffectType.LEVITATION,10,1,true,false);
        p.addPotionEffect(effect);
    }
}
