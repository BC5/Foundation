package uk.lsuth.mc.foundation.fabric;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.Module;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class FabricModule implements Module
{
    FoundationCore core;

    private ArrayList<UUID> fabricPlayers;

    public FabricModule(FoundationCore core)
    {
        this.core = core;
        this.fabricPlayers = new ArrayList<UUID>();
    }

    @Override
    public List<FoundationCommand> getCommands()
    {
        ArrayList<FoundationCommand> commandList = new ArrayList<FoundationCommand>();
        commandList.add(new Toast(core,this));

        return commandList;
    }

    @Override
    public List<Listener> getListeners()
    {
        ArrayList<Listener> listenerList = new ArrayList<Listener>();

        listenerList.add(new HandshakeListener(core,this));

        return listenerList;
    }

    @Override
    public HashMap<String, Object> getTemplateData()
    {
        return new HashMap<String,Object>();
    }

    public boolean isFabricPlayer(Player p)
    {
        for(UUID u:fabricPlayers)
        {
            if(p.getUniqueId().equals(u))
            {
                return true;
            }
        }
        return false;
    }

    public void setFabricPlayer(Player p)
    {
        fabricPlayers.add(p.getUniqueId());
    }

    public void removeFabricPlayer(Player p)
    {
        UUID toRemove = null;
        for(UUID u:fabricPlayers)
        {
            if(p.getUniqueId().equals(u))
            {
                toRemove = u;
            }
        }

        if(toRemove != null)
        {
            fabricPlayers.remove(toRemove);
        }

    }

    public static ByteBuffer writeVarInt(ByteBuffer buf, int v)
    {
        byte[] bytes = new byte[0];
        do
        {
            byte temp = (byte)(v & 0b01111111);
            v >>>= 7;
            if(v != 0)
            {
                temp |= 0b10000000;
            }
            buf.put(temp);
        }
        while (v != 0);

        return buf;
    }

}
