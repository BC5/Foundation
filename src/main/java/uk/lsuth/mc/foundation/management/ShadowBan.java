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
import org.bukkit.plugin.Plugin;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class ShadowBan extends FoundationCommand
{
    Map<String,String> strings;
    ShadowBanAdapter adapter;


    public ShadowBan(FoundationCore core)
    {
        super("shadowban");
        ProtocolManager pman = ProtocolLibrary.getProtocolManager();
        adapter = new ShadowBanAdapter(core,core.log);
        pman.addPacketListener(adapter);
        strings = core.getLmgr().getCommandStrings("shadowban");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(!(sender.hasPermission("foundation.management.shadowban")))
        {
            sender.sendMessage(FoundationCore.noPermission);
            return true;
        }

        Server server = Bukkit.getServer();
        Player p = server.getPlayer(args[0]);

        if(p != null)
        {
            if(!adapter.isBanned(p))
            {
                adapter.ban(p);
                sender.sendMessage(strings.get("ban").replace("{x}",p.getDisplayName()));
            }
            else
            {
                adapter.unban(p);
                sender.sendMessage(strings.get("unban").replace("{x}",p.getDisplayName()));
            }
        }
        else
        {
            OfflinePlayer p2 = server.getOfflinePlayer(args[0]);
            if(!adapter.isBanned(p2))
            {
                adapter.ban(p2);
                sender.sendMessage(strings.get("ban").replace("{x}",p.getDisplayName()));
            }
            else
            {
                adapter.unban(p2);
                sender.sendMessage(strings.get("unban").replace("{x}",p.getDisplayName()));
            }
            sender.sendMessage(strings.get("noSuchPlayer"));
        }
        return true;

    }

    private class ShadowBanAdapter extends PacketAdapter
    {
        private ArrayList<UUID> banList;
        private Logger log;


        ShadowBanAdapter(Plugin p, Logger log)
        {
            super(p, ListenerPriority.NORMAL, PacketType.Play.Server.LOGIN);
            banList = new ArrayList<UUID>();
            this.log = log;
        }

        @Override
        public void onPacketSending(PacketEvent e)
        {
            if(banList.size() != 0)
            {
                if(isBanned(e.getPlayer()))
                {
                    e.setCancelled(true);
                }
            }
        }

        @Override
        public void onPacketReceiving(PacketEvent e)
        {
            if(banList.size() != 0)
            {
                if(isBanned(e.getPlayer()))
                {
                    e.setCancelled(true);
                }
            }
        }

        public boolean isBanned(UUID u)
        {
            for(UUID id: banList)
            {
                if(u.equals(id))
                {
                    return true;
                }
            }
            return false;
        }

        public boolean isBanned(OfflinePlayer p)
        {
            return isBanned(p.getUniqueId());
        }

        public void ban(UUID u)
        {
            banList.add(u);
        }

        public void ban(OfflinePlayer u)
        {
            banList.add(u.getUniqueId());
        }

        public void unban(UUID u)
        {
            banList.remove(u);
        }

        public void unban(OfflinePlayer u)
        {
            banList.remove(u.getUniqueId());
        }

    }
}
