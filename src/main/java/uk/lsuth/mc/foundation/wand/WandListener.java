package uk.lsuth.mc.foundation.wand;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class WandListener implements Listener
{
    @EventHandler
    private void use(PlayerInteractEvent event)
    {
        Player p = event.getPlayer();
        ItemStack hand = p.getInventory().getItemInMainHand();

        if(hand.getType() == Material.STICK)
        {

        }
    }
}
