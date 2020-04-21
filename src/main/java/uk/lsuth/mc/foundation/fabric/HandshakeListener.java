package uk.lsuth.mc.foundation.fabric;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import uk.lsuth.mc.foundation.FoundationCore;

public class HandshakeListener implements PluginMessageListener, Listener
{
    FoundationCore core;
    FabricModule module;

    public final String channel = "foundation:handshake";

    public HandshakeListener(FoundationCore core, FabricModule module)
    {
        this.core = core;
        this.module = module;
        this.register();
    }

    private void register()
    {
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(core,channel,this);
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(core,channel);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message)
    {
        String msg = "";
        for(int i = 1; i < message.length; i++)
        {
            msg = msg + (char)(message[i]);
        }
        player.sendPluginMessage(core,channel,("HELLO " + player.getName()).getBytes());

        module.setFabricPlayer(player);
        core.log.info(player.getDisplayName() + " added to FoundationFabric list");
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent e)
    {
        module.removeFabricPlayer(e.getPlayer());
    }
}

