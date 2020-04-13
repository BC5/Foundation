package uk.lsuth.mc.foundation.beacon;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.Module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BeaconUtils implements Module
{
    FoundationCore core;

    public BeaconUtils(FoundationCore core)
    {
        this.core = core;
    }

    public static int getBeaconTier(Block beacon)
    {
        Beacon beaconState = (Beacon) beacon.getState();
        return beaconState.getTier();
    }

    public static boolean beaconIsTier(Block beacon, int tier, Material material)
    {
        int t2 = tier;

        if(t2 > 4)
        {
            t2 = 4;
        }

        if(getBeaconTier(beacon) >= t2)
        {
            World w = beacon.getWorld();
            Location location = beacon.getLocation();
            for(int i = 0; i < tier; i++)
            {
                Location loc0 = location.clone().subtract(1+i,1+i,1+i);
                for(int deltaX = 0; deltaX < 3+(2*i); deltaX++)
                {
                    for(int deltaZ = 0; deltaZ < 3+(2*i); deltaZ++)
                    {
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

    @Override
    public List<FoundationCommand> getCommands()
    {
        return new ArrayList<>();
    }

    @Override
    public List<Listener> getListeners()
    {
        ArrayList<Listener> listenerList = new ArrayList<Listener>();
        listenerList.add(new LevitationBeacon());
        listenerList.add(new PhantomDisabler(core));
        return listenerList;
    }

    @Override
    public HashMap<String, Object> getTemplateData()
    {
        return new HashMap<String, Object>();
    }
}
