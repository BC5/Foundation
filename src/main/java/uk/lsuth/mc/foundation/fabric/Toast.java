package uk.lsuth.mc.foundation.fabric;

import com.google.common.base.Charsets;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;

import java.nio.ByteBuffer;
import java.util.Map;

public class Toast extends FoundationCommand
{
    FoundationCore core;
    FabricModule module;
    Map<String,String> strings;

    public final String channel = "foundation:toast";

    public Toast(FoundationCore core, FabricModule module)
    {
        super("toast");
        this.core = core;
        this.module = module;
        this.strings = core.getLmgr().getCommandStrings("toast");
        register();
    }

    private void register()
    {
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(core,channel);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(args.length == 0)
        {
            return false;
        }

        if(sender instanceof Player)
        {
            Player player = (Player) sender;

            if(module.isFabricPlayer(player))
            {
                String msg = "";
                for(int i = 0; i < args.length; i++)
                {
                    if(i == args.length - 1)
                    {
                        msg = msg + args[i];
                    }
                    else
                    {
                        msg = msg + args[i] + " ";
                    }
                }

                byte[] bytes = msg.getBytes(Charsets.UTF_8);
                ByteBuffer bb = ByteBuffer.allocate(1024);
                FabricModule.writeVarInt(bb,bytes.length);
                bb.put(bytes);

                player.sendPluginMessage(core,channel,bb.array());
            }
            else
            {
                sender.sendMessage(strings.get("notFabric"));
            }
            return true;
        }
        else
        {
            return false;
        }
    }
}
