package uk.lsuth.mc.foundation.beacon;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class BeaconUtils
{
    public static boolean beaconIsTier(Block beacon, int tier, Material material)
    {
        Beacon beaconState = (Beacon) beacon.getState();

        int t2 = tier;

        if(t2 > 4)
        {
            t2 = 4;
        }

        if(beaconState.getTier() == t2)
        {
            World w = beacon.getWorld();
            Location location = beacon.getLocation();
            for(int i = 0; i < tier; i++)
            {
                Location loc0 = location.clone().subtract(1+i,1+i,1+i);
                System.out.println("i:" + i);
                for(int deltaX = 0; deltaX < 3+(2*i); deltaX++)
                {
                    System.out.println("dX:" + deltaX);
                    for(int deltaZ = 0; deltaZ < 3+(2*i); deltaZ++)
                    {
                        System.out.println("dZ:" + deltaZ);
                        if (!(w.getBlockAt(loc0.clone().add(deltaX, 0, deltaZ)).getType() == material))
                        {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    public static Block getBlockBelow(Player p)
    {
        return p.getWorld().getBlockAt(p.getLocation().subtract(0,1,0));
    }

    public static boolean isStandingOnBeacon(Player p)
    {
        Block b = getBlockBelow(p);
        if(b != null)
        {
            if(b.getType() == Material.BEACON)
            {
                return true;
            }
        }
        return false;
    }
}
