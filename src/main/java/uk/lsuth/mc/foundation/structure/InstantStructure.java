package uk.lsuth.mc.foundation.structure;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public abstract class InstantStructure
{
    public abstract Material[][][] getBlocks();

    public abstract int getXOffset();
    public abstract int getYOffset();
    public abstract int getZOffset();

    public void assemble(Player p, int aX,int aY,int aZ)
    {
        Location loc = p.getLocation();
        World w = loc.getWorld();

        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        int offsetX = getXOffset() + aX;
        int offsetY = getYOffset() + aY;
        int offsetZ = getZOffset() + aZ;

        Material[][][] blocks = getBlocks();

        for(int i = 0; i < blocks.length; i++)
        {
            for(int j = 0; j < blocks[i].length; j++)
            {
                for(int k = 0; k < blocks[i][j].length; k++)
                {
                    w.getBlockAt(x+offsetX+k,y+offsetY+i,z+offsetZ+j).setType(blocks[i][j][k]);
                }
            }
        }

    }
}
