package uk.lsuth.mc.foundation.world;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;
import uk.lsuth.mc.foundation.FoundationCore;

import java.util.ArrayList;
import java.util.Random;

public class BiomeChange implements Listener
{
    private Random rng;
    private int chance;
    private boolean onlyOverworld;
    private boolean onBonemeal;
    private boolean onNaturalGrowth;

    public BiomeChange(FoundationCore core)
    {
        rng = new Random();
        chance = core.getConfiguration().getInt("biomeChange.chance");
        onlyOverworld = core.getConfiguration().getBoolean("biomeChange.onlyOverworld");
        onBonemeal = core.getConfiguration().getBoolean("biomeChange.bonemeal");
        onNaturalGrowth = core.getConfiguration().getBoolean("biomeChange.naturalGrowth");
    }


    @EventHandler
    public void treeGrowth(StructureGrowEvent e)
    {
        if(e.isFromBonemeal() )
        {
            if(!onBonemeal) return;
        }
        else
        {
            if(!onNaturalGrowth) return;
        }

        if(rng.nextInt(101) > chance)
        {
            Location loc = e.getLocation();
            World w = loc.getWorld();
            if(w.getEnvironment() == World.Environment.NORMAL || !onlyOverworld)
            {
                Biome newBiome = treeBiome(e.getSpecies());
                Biome existingBiome = w.getBiome(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ());

                if(newBiome.equals(existingBiome))
                {
                    return;
                }
                else
                {
                    initiateBiomeChange(loc,newBiome);
                }
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

    private static void initiateBiomeChange(Location loc, Biome newBiome)
    {
        World w = loc.getWorld();
        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        ArrayList<Chunk> updateList = new ArrayList<Chunk>();

        for(int column = -3; column <= 3; column++)
        {
            int columnSize = 0;

            switch (Math.abs(column))
            {
                case 0:
                case 1:
                    columnSize = 3;
                    break;
                case 2:
                    columnSize = 2;
                    break;
                case 3:
                    columnSize = 1;
                    break;
            }

            for(int row = -columnSize; row <= columnSize; row++)
            {
                //TODO: understand why this is deprecated and fix it.
                w.setBiome(x + column, z + row, newBiome);
                Block b = w.getHighestBlockAt(x+column,z+row);
                Location particleLocation = b.getLocation();
                w.spawnParticle(Particle.COMPOSTER,particleLocation.add(0.5,1.5,0.5),5);
            }

        }
    }

    private static Biome treeBiome(TreeType t)
    {
        switch (t)
        {
            case SMALL_JUNGLE:
            case JUNGLE:
            case COCOA_TREE:
            case JUNGLE_BUSH:
                return Biome.JUNGLE;
            case BIRCH:
            case TREE:
            case BIG_TREE:
                return Biome.FOREST;
            case BROWN_MUSHROOM:
            case RED_MUSHROOM:
                return Biome.MUSHROOM_FIELDS;
            case ACACIA:
                return Biome.SAVANNA;
            case DARK_OAK:
                return Biome.DARK_FOREST;
            case REDWOOD:
            case TALL_REDWOOD:
                return Biome.TAIGA;
            case MEGA_REDWOOD:
                return Biome.GIANT_SPRUCE_TAIGA;
            case SWAMP:
                return Biome.SWAMP;
            case TALL_BIRCH:
                return Biome.TALL_BIRCH_FOREST;
            case CHORUS_PLANT:
                return Biome.THE_END;
            default:
                return null;
        }
    }
}
