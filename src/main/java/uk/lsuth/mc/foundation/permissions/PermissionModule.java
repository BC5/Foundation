package uk.lsuth.mc.foundation.permissions;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.Module;

import java.util.HashMap;
import java.util.List;

public class PermissionModule implements Module
{

    FoundationCore core;

    public PermissionModule(FoundationCore core)
    {
        this.core = core;
    }

    @Override
    public List<FoundationCommand> getCommands()
    {
        return null;
    }

    @Override
    public HashMap<String, Object> getTemplateData()
    {
        return null;
    }
}
