package uk.lsuth.mc.foundation.railroute;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Rail;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import uk.lsuth.mc.foundation.FoundationCore;

public class Junction
{

    public Junction(FoundationCore core)
    {
        ProtocolManager pman = ProtocolLibrary.getProtocolManager();
        pman.addPacketListener(new JunctionAdapter(core));
    }



    private class JunctionAdapter extends PacketAdapter
    {
        JunctionAdapter(Plugin p)
        {
            super(p, ListenerPriority.NORMAL, PacketType.Play.Client.STEER_VEHICLE);
        }

        public void onPacketSending(PacketEvent e)
        {
            return;
        }

        public void onPacketReceiving(PacketEvent e)
        {
            Player player = e.getPlayer();
            if(!player.isInsideVehicle())
            {
                return;
            }

            if(player.getVehicle() instanceof Minecart)
            {
                if(!(player.hasPermission("foundation.rail.junction")))
                {
                    player.sendActionBar(FoundationCore.noPermission);
                    return;
                }

                Minecart minecart = (Minecart) player.getVehicle();
                Vector v = minecart.getVelocity();

                if(!Cardinal.isCardinal(v)) return;

                Cardinal direction = Cardinal.getCardinal(v);

                Block under = minecart.getWorld().getBlockAt(minecart.getLocation()).getRelative(0,-1,0);
                Block block = null;

                for(int i = 0; i <= 8; i++)
                {
                    block = under.getRelative(direction.x*i,0,direction.z*i);
                    if(block.getType() == Material.GOLD_BLOCK)
                    {
                        break;
                    }
                }

                if(block.getType() == Material.GOLD_BLOCK)
                {
                    Block r = block.getRelative(0,1,0);
                    if(r.getType() == Material.RAIL)
                    {
                        BlockState railState = r.getState();
                        Rail rail = (Rail) railState.getBlockData();

                        StructureModifier<Float> x =  e.getPacket().getFloat();
                        float side = x.read(0);
                        float forwd = x.read(1);

                        if(side != 0)
                        {
                            if(side > 0)
                            {
                                player.sendActionBar("Steering left");
                                rail.setShape(Cardinal.Rail(Cardinal.opp(direction), Cardinal.ccw(direction)));
                            }
                            else
                            {
                                player.sendActionBar("Steering right");
                                rail.setShape(Cardinal.Rail(Cardinal.opp(direction), Cardinal.cw(direction)));
                            }
                        }
                        else if(forwd != 0)
                        {
                            player.sendActionBar("Steering forward");

                            if(direction == Cardinal.NORTH || direction == Cardinal.SOUTH)
                            {
                                rail.setShape(Rail.Shape.NORTH_SOUTH);
                            }
                            else
                            {
                                rail.setShape(Rail.Shape.EAST_WEST);
                            }
                        }
                        else
                        {
                            return;
                        }


                        BukkitRunnable br = new BukkitRunnable()
                        {
                            @Override
                            public void run()
                            {
                                railState.setBlockData(rail);
                                railState.update();
                            }
                        };

                        //Stops async errors
                        br.runTask(this.getPlugin());
                    }
                }

            }
        }
    }

    public enum Cardinal
    {
        NORTH(0,-1),
        SOUTH(0,1),
        EAST(1,0),
        WEST(0,1);

        int x;
        int z;

        Cardinal(int x, int z)
        {
            this.x = x;
            this.z = z;
        }

        public static Cardinal cw(Cardinal c)
        {
            switch (c)
            {
                case NORTH:
                    return EAST;
                case EAST:
                    return SOUTH;
                case SOUTH:
                    return WEST;
                case WEST:
                    return NORTH;
                default:
                    return null;
            }
        }

        public static Rail.Shape Rail(Cardinal a, Cardinal b)
        {
            switch(a)
            {
                case NORTH:
                    switch(b)
                    {
                        case NORTH:
                        case SOUTH:
                            return Rail.Shape.NORTH_SOUTH;
                        case EAST:
                            return Rail.Shape.NORTH_EAST;
                        case WEST:
                            return Rail.Shape.NORTH_WEST;
                        default:
                            return null;
                    }
                case EAST:
                    switch(b)
                    {
                        case NORTH:
                            return Rail.Shape.NORTH_EAST;
                        case SOUTH:
                            return Rail.Shape.SOUTH_EAST;
                        case EAST:
                        case WEST:
                            return Rail.Shape.EAST_WEST;
                        default:
                            return null;
                    }
                case SOUTH:
                    switch(b)
                    {
                        case NORTH:
                        case SOUTH:
                            return Rail.Shape.NORTH_SOUTH;
                        case EAST:
                            return Rail.Shape.SOUTH_EAST;
                        case WEST:
                            return Rail.Shape.SOUTH_WEST;
                        default:
                            return null;
                    }
                case WEST:
                    switch(b)
                    {
                        case NORTH:
                            return Rail.Shape.NORTH_WEST;
                        case SOUTH:
                            return Rail.Shape.SOUTH_WEST;
                        case EAST:
                        case WEST:
                            return Rail.Shape.EAST_WEST;
                        default:
                            return null;
                    }
                default:
                    return null;
            }
        }

        public static Cardinal ccw(Cardinal c)
        {
            switch (c)
            {
                case NORTH:
                    return WEST;
                case EAST:
                    return NORTH;
                case SOUTH:
                    return EAST;
                case WEST:
                    return SOUTH;
                default:
                    return null;
            }
        }

        public static Cardinal opp(Cardinal c)
        {
            switch (c)
            {
                case NORTH:
                    return SOUTH;
                case EAST:
                    return WEST;
                case SOUTH:
                    return NORTH;
                case WEST:
                    return EAST;
                default:
                    return null;
            }
        }

        public static Cardinal getCardinal(Vector v)
        {
            if(v.getX() != 0)
            {
                if(v.getX() > 0)
                {
                    return EAST;
                }
                else
                {
                    return WEST;
                }
            }
            else
            {
                if(v.getZ() > 0)
                {
                    return SOUTH;
                }
                else
                {
                    return NORTH;
                }
            }
        }

        public static boolean isCardinal(Vector v)
        {
            //Only proceed if travelling cardinal direction
            if(v.getX() != 0 && v.getZ() != 0 || v.getY() != 0)
            {
                return false;
            }
            return true;
        }

    }
}
