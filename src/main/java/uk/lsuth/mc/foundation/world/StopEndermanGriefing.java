package uk.lsuth.mc.foundation.world;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class StopEndermanGriefing implements Listener
{
    @EventHandler
    public void onEnderPlace(EntityBlockFormEvent placeEvent)
    {
        if(placeEvent.getEntity().getType() == EntityType.ENDERMAN)
        {
            placeEvent.setCancelled(true);
        }
    }

    @EventHandler
    public void onEnderPickup(EntityChangeBlockEvent breakEvent)
    {
        if(breakEvent.getEntityType() == EntityType.ENDERMAN)
        {
            breakEvent.setCancelled(true);
        }
    }
}
