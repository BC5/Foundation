package uk.lsuth.mc.foundation.structure;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.Module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Prefab extends FoundationCommand implements Module
{
    public Prefab()
    {
        super("prefab");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {

        if(!(sender.hasPermission("foundation.prefab")))
        {
            sender.sendMessage(FoundationCore.noPermission);
            return true;
        }

        if(args.length <= 0 || args.length > 4)
        {
            return false;
        }
        else
        {
            int[] offset = {0,0,0};
            for(int i = 1;i < args.length;i++)
            {
                offset[i-1] = Integer.parseInt(args[i]);
            }


            switch(args[0])
            {
                case "hut":
                    new InstantHouse().assemble((Player) sender,offset[0],offset[1],offset[2]);
                    return true;
                default:
                    return false;
            }
        }
    }

    @Override
    public List<FoundationCommand> getCommands()
    {
        ArrayList x = new ArrayList();
        x.add(this);
        return x;
    }

    @Override
    public HashMap<String, Object> getTemplateData()
    {
        return new HashMap<String,Object>();
    }
}
