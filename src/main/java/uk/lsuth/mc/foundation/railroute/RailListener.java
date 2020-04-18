package uk.lsuth.mc.foundation.railroute;

import com.google.common.base.Predicates;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Rail;
import org.bukkit.block.data.type.RedstoneRail;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import uk.lsuth.mc.foundation.FoundationCore;

import java.util.Collection;
import java.util.function.Predicate;

public class RailListener implements Listener
{
    FoundationCore core;

    private static final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.YELLOW,1);

    public RailListener(FoundationCore core)
    {
        this.core = core;
    }

    @EventHandler
    private void minecartMove(VehicleMoveEvent e)
    {
        if(e.getVehicle() instanceof Minecart)
        {
            Minecart minecart = (Minecart) e.getVehicle();
            World world = minecart.getWorld();
            Location location = minecart.getLocation();
            location.setY(location.getY()-1);
            Block blockBelow = location.getBlock();

            if(blockBelow.getType() == Material.IRON_BLOCK)
            {
                minecart.setMaxSpeed(0.4d * 8);
                //System.out.println(minecart.getVelocity().length());
                world.spawnParticle(Particle.REDSTONE,minecart.getLocation(),5,0.05d,0.2d,0.05d,dustOptions);
            }
            else
            {
                minecart.setMaxSpeed(0.4d);
            }
        }
    }

    /*
    @EventHandler
    private void minecartRunOver(VehicleEntityCollisionEvent e)
    {
        if(e.getVehicle() instanceof Minecart)
        {
            Minecart minecart = (Minecart) e.getVehicle();
            Double speed = minecart.getVelocity().length();
            if(speed > 2)
            {
                Entity entity = e.getEntity();
                if(entity instanceof LivingEntity)
                {
                    LivingEntity livingEntity = (LivingEntity) entity;

                    livingEntity.damage((speed-2)*10,minecart);
                    if(livingEntity.isDead())
                    {
                        e.setCollisionCancelled(true);
                    }
                }
            }

        }
    }

    @EventHandler
    private void death(PlayerDeathEvent e)
    {
        Player victim = e.getEntity();
        if(victim.getLastDamageCause() instanceof EntityDamageByEntityEvent)
        {
            if(((EntityDamageByEntityEvent) victim.getLastDamageCause()).getDamager() instanceof Minecart)
            {
                Minecart minecart = (Minecart) victim.getKiller();
                if (minecart.getPassengers().size() != 0 && minecart.getPassengers().get(0) instanceof Player)
                {
                    Player murderer = (Player) minecart.getPassengers().get(0);

                    e.setDeathMessage(murderer.getDisplayName() + " ran over " + victim.getDisplayName());
                    victim.setKiller(murderer);
                }
                else
                {
                    e.setDeathMessage(victim.getDisplayName() + " was ran over by a minecart");
                }
            }
        }
    }
    */

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
