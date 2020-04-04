package uk.lsuth.mc.foundation.data;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldSaveEvent;
import uk.lsuth.mc.foundation.FoundationCore;

/**
 * Triggers loading and unloading of player data
 */
public class PlayerListener implements Listener
{
    DataManager dmgr;

    public PlayerListener(FoundationCore core)
    {
        this.dmgr = core.dmgr;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onLogin(PlayerJoinEvent e)
    {
        dmgr.loadPlayer(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDisconnect(PlayerQuitEvent e)
    {
        dmgr.unloadPlayer(e.getPlayer());
    }

    @EventHandler
    public void onWorldSave(WorldSaveEvent e)
    {
        if(e.getWorld() == Bukkit.getWorlds().get(0))
        {
            dmgr.stash();
        }

    }

}
