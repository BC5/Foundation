package uk.lsuth.mc.foundation.enchant;

import org.bukkit.event.player.PlayerInteractEvent;

public interface FoundationEnchant
{
    String getName();

    void handle(int level, PlayerInteractEvent event);
}
