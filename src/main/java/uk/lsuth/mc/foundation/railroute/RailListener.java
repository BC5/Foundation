package uk.lsuth.mc.foundation.railroute;

import com.google.common.base.Predicates;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Rail;
import org.bukkit.block.data.type.RedstoneRail;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import uk.lsuth.mc.foundation.FoundationCore;

import java.util.Collection;
import java.util.function.Predicate;

public class RailListener implements Listener
{
    FoundationCore core;

    public RailListener(FoundationCore core)
    {
        this.core = core;
    }

    @EventHandler
    private void detectorRail(EntityInteractEvent event)
    {
        if(event.getEntityType() == EntityType.MINECART)
        {
            if(event.getBlock().getType() == Material.DETECTOR_RAIL)
            {
                System.out.println("test");
                event.setCancelled(true);
            }

        }
    }

    @EventHandler
    private void redstoneChange(BlockRedstoneEvent event)
    {

        Block b = event.getBlock();

        if(b.getType() == Material.DETECTOR_RAIL)
        {
            //Check if block below rail is a Lapis block
            if(b.getRelative(0,-1,0).getType() == Material.LAPIS_BLOCK)
            {
                //Getting nearby minecarts
                World w = b.getWorld();
                Predicate minecartFilter = Predicates.instanceOf(Minecart.class);
                Collection<Entity> minecarts = w.getNearbyEntities(b.getLocation(),1.5,1.5,1.5,minecartFilter);

                if(minecarts.size() == 0)
                {
                    //If none found, ignore.
                    return;
                }
                else
                {
                    Minecart cart = (Minecart) minecarts.iterator().next();
                    Vector velocity = cart.getVelocity();
                    if(velocity.length() > 0)
                    {
                        double velocityX = velocity.getX();
                        double velocityZ = velocity.getZ();

                        //NORTH-SOUTH
                        if(velocityZ != 0 && velocityX == 0)
                        {
                            new BukkitRunnable()
                            {
                                @Override
                                public void run()
                                {
                                    //System.out.println("Redirect NS");

                                    BlockState state = b.getState();

                                    RedstoneRail data = (RedstoneRail) state.getBlockData();
                                    data.setShape(Rail.Shape.NORTH_SOUTH);

                                    state.setBlockData(data);
                                    state.update();
                                }
                            }.runTaskLater(core,1);
                        }

                        //EAST-WEST
                        if(velocityX != 0 && velocityZ == 0)
                        {
                            new BukkitRunnable()
                            {
                                @Override
                                public void run()
                                {
                                    //System.out.println("Redirect EW");

                                    BlockState state = b.getState();

                                    RedstoneRail data = (RedstoneRail) state.getBlockData();
                                    data.setShape(Rail.Shape.EAST_WEST);

                                    state.setBlockData(data);
                                    state.update();
                                }
                            }.runTaskLater(core,1);
                        }
                    }
                }
            }
        }
    }
}
