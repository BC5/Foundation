package uk.lsuth.mc.foundation.management;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class ShadowKick extends FoundationCommand
{
    Map<String,String> strings;
    ShadowKickAdapter adapter;


    public ShadowKick(FoundationCore core)
    {
        super("shadowkick");
        ProtocolManager pman = ProtocolLibrary.getProtocolManager();
        adapter = new ShadowKickAdapter(core,core.log);
        pman.addPacketListener(adapter);
        strings = core.getLmgr().getCommandStrings("shadowkick");
    }

    public Listener getListener()
    {
        return adapter;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(!(sender.hasPermission("foundation.management.shadowkick")))
        {
            sender.sendMessage(FoundationCore.noPermission);
            return true;
        }

        Server server = Bukkit.getServer();
        Player toKick = server.getPlayer(args[0]);

        if(toKick != null)
        {
            if(!adapter.isBeingKicked(toKick))
            {
                adapter.kick(toKick);
                sender.sendMessage(strings.get("success").replace("{x}",toKick.getDisplayName()));
            }
            else
            {
                sender.sendMessage(strings.get("alreadyKicking").replace("{x}",toKick.getDisplayName()));
            }
        }
        else
        {
            sender.sendMessage(strings.get("noSuchPlayer"));
        }
        return true;

    }

    private class ShadowKickAdapter extends PacketAdapter implements Listener
    {
        private ArrayList<UUID> toKick;
        private Logger log;


        ShadowKickAdapter(Plugin p, Logger log)
        {
            super(p, ListenerPriority.NORMAL, PacketType.Play.Server.KEEP_ALIVE);
            toKick = new ArrayList<UUID>();
            this.log = log;
        }

        @EventHandler
        public void onDisconnect(PlayerQuitEvent e)
        {
            if(toKick.size() != 0)
            {
                if(isBeingKicked(e.getPlayer()))
                {
                    log.info(e.getPlayer().getDisplayName() + " has been successfully shadowkicked");
                    removeFromKickList(e.getPlayer());
                }
            }
        }

        @Override
        public void onPacketSending(PacketEvent e)
        {
            if(toKick.size() != 0)
            {
                if(isBeingKicked(e.getPlayer()))
                {
                    e.setCancelled(true);
                }
            }
        }

        @Override
        public void onPacketReceiving(PacketEvent e)
        {
            //No action on receipt.
            return;
        }

        public boolean isBeingKicked(UUID u)
        {
            for(UUID id:toKick)
            {
                if(u.equals(id))
                {
                    return true;
                }
            }
            return false;
        }

        public boolean isBeingKicked(OfflinePlayer p)
        {
            return isBeingKicked(p.getUniqueId());
        }

        public void kick(UUID u)
        {
            toKick.add(u);
        }

        public void kick(OfflinePlayer u)
        {
            toKick.add(u.getUniqueId());
        }

        public void removeFromKickList(UUID u)
        {
            toKick.remove(u);
        }

        public void removeFromKickList(OfflinePlayer u)
        {
            toKick.remove(u.getUniqueId());
        }

    }
}
