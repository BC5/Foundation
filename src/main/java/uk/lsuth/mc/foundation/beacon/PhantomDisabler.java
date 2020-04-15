package uk.lsuth.mc.foundation.beacon;

import com.destroystokyo.paper.event.block.BeaconEffectEvent;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Phantom;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import uk.lsuth.mc.foundation.FoundationCore;

import java.util.List;

public class PhantomDisabler implements Listener
{
    FoundationCore core;

    public PhantomDisabler(FoundationCore core)
    {
        this.core = core;
    }


    private static final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.PURPLE,1);

    @EventHandler
    public void onBeaconEffectApplied(BeaconEffectEvent e)
    {
        Block beacon = e.getBlock();

        int level = BeaconUtils.getBeaconTier(beacon);

        List<Entity> entityList = e.getPlayer().getNearbyEntities(level*15,level*15,level*15);

        for(Entity entity:entityList)
        {
            if(entity.getType() == EntityType.PHANTOM)
            {
                Phantom phantom = (Phantom) entity;
                phantom.getWorld().strikeLightningEffect(phantom.getLocation());
                phantom.remove();

                new BukkitRunnable()
                {

                    int runs = 0;

                    @Override
                    public void run()
                    {
                        beacon.getWorld().spawnParticle(Particle.REDSTONE,beacon.getLocation().add(0.5,5,0.5),50,0.05,5,0.05,dustOptions);
                        if(runs >= 20)
                        {
                            this.cancel();
                        }
                        else
                        {
                            runs++;
                        }
                    }
                }.runTaskTimer(core,0,1);



                beacon.getLocation();
            }
        }
    }
}
