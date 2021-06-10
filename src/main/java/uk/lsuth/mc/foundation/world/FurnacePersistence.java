package uk.lsuth.mc.foundation.world;


import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import uk.lsuth.mc.foundation.FoundationCore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class FurnacePersistence implements Listener
{
    Logger log;
    FoundationCore core;
    ArrayList<String> chunks;

    public FurnacePersistence(FoundationCore core)
    {
        this.core = core;
        this.log = core.log;
        chunks = new ArrayList<String>();
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e)
    {
        Chunk c = e.getChunk();


        Predicate<Block> filter = (b) -> {
            switch (b.getType())
            {
                case FURNACE:
                case BLAST_FURNACE:
                case SMOKER:
                    return true;
                default:
                    return false;
            }
        };

        Collection<BlockState> furnaces = c.getTileEntities(filter,false);

        if(furnaces.size() == 0)
        {
            return;
        }
        else
        {
            boolean foundLitFurnace = false;
            for(BlockState s: furnaces)
            {
                Furnace f = (Furnace) s;
                if(f.getBurnTime() != 0)
                {
                    foundLitFurnace = true;
                    break;
                }
            }

            if(!foundLitFurnace) return;
            String chunkid = e.getWorld().getName() + ":" + c.getX() + "," + c.getZ();

            for(int i = 0; i < chunks.size(); i++)
            {
                if(chunkid.equals(chunks.get(i)))
                {
                    chunks.remove(i);
                    log.info("Unloaded " + chunkid);
                    return;
                }
            }

            c.addPluginChunkTicket(core);
            log.info("Given chunk " + chunkid + " another 5 minutes of loaded time due to presence of lit furnace");
            chunks.add(chunkid);

            Bukkit.getScheduler().runTaskLater(core,()->{
                c.removePluginChunkTicket(core);
                log.info("Removed ticket for " + chunkid);
                },6000);

        }
    }

}
