package uk.lsuth.mc.foundation.data;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Triggers loading and unloading of player data
 */
public class PlayerListener implements Listener
{
    DataManager dm;

    public PlayerListener(DataManager dm)
    {
        this.dm = dm;
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent e)
    {
        dm.loadPlayer(e.getPlayer());
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e)
    {
        dm.unloadPlayer(e.getPlayer());
    }

}
