package uk.lsuth.mc.foundation.structure;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InstantHouse extends InstantStructure
{
    @Override
    public int getXOffset()
    {
        return -2;
    }

    @Override
    public int getYOffset()
    {
        return -1;
    }

    @Override
    public int getZOffset()
    {
        return -2;
    }

    public Material[][][] getBlocks()
    {
        Material f = Material.OAK_PLANKS;
        Material a = Material.AIR;
        Material s = Material.COBBLESTONE;
        Material c = Material.CRAFTING_TABLE;
        Material o = Material.FURNACE;
        Material h = Material.COBBLESTONE_SLAB;
        Material t = Material.TORCH;


        Material[][] y0 =
        {
            {f,f,f,f,f},
            {f,f,f,f,f},
            {f,f,f,f,f},
            {f,f,f,f,f},
            {f,f,f,f,f}
        };

        Material[][] y1 =
        {
            {s,s,s,s,s},
            {s,c,o,a,s},
            {s,a,a,a,s},
            {s,a,a,a,s},
            {s,s,a,s,s}
        };

        Material[][] y2 =
        {
            {s,s,s,s,s},
            {s,a,a,a,s},
            {s,a,a,a,s},
            {s,a,a,a,s},
            {s,s,a,s,s}
        };

        Material[][] y3 =
        {
            {s,s,s,s,s},
            {s,a,t,a,s},
            {s,a,a,a,s},
            {s,a,a,a,s},
            {s,s,s,s,s}
        };

        Material[][] y4 =
        {
            {h,h,h,h,h},
            {h,h,h,h,h},
            {h,h,h,h,h},
            {h,h,h,h,h},
            {h,h,h,h,h}
        };

        return new Material[][][] {y0, y1, y2, y3, y4};

    }
}
