package uk.lsuth.mc.foundation.essentialcommands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Spectate extends FoundationCommand implements Listener
{
    HashMap<UUID, Location> locations;
    HashMap<UUID, SpectatorTask> tasks;

    Map<String,String> strings;

    FoundationCore core;

    public Spectate(FoundationCore core)
    {
        super("spectate");
        locations = new HashMap<UUID,Location>();
        tasks = new HashMap<UUID, SpectatorTask>();
        this.core = core;
        strings = core.getLmgr().getCommandStrings("spectate");
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e)
    {
        if(locations.containsKey(e.getPlayer().getUniqueId()))
        {
            Player player = e.getPlayer();

            player.teleport(locations.get(player.getUniqueId()));
            player.setGameMode(GameMode.SURVIVAL);
            player.sendMessage(strings.get("survival"));
            locations.remove(player.getUniqueId());

            if(tasks.containsKey(player.getUniqueId()))
            {
                SpectatorTask task = tasks.get(player.getUniqueId());
                task.cancel();
                tasks.remove(player.getUniqueId());
            }
        }
        else
        {
            return;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(sender instanceof Player)
        {
            Player player = (Player) sender;

            if(!player.hasPermission("foundation.spectator"))
            {
                player.sendMessage(FoundationCore.noPermission);
                return true;
            }

            if(player.getGameMode() == GameMode.SPECTATOR)
            {
                if(locations.containsKey(player.getUniqueId()))
                {
                    player.teleport(locations.get(player.getUniqueId()));
                    locations.remove(player.getUniqueId());
                }
                if(tasks.containsKey(player.getUniqueId()))
                {
                    SpectatorTask task = tasks.get(player.getUniqueId());
                    task.cancel();
                    tasks.remove(player.getUniqueId());
                }
                player.setGameMode(GameMode.SURVIVAL);
                player.sendMessage(strings.get("survival"));

            }
            else
            {
                if(player.getHealth() > 19)
                {
                    locations.put(player.getUniqueId(),player.getLocation());
                    player.setGameMode(GameMode.SPECTATOR);
                    player.sendMessage(strings.get("spectator"));

                    SpectatorTask task = new SpectatorTask(player.getUniqueId());
                    task.runTaskLater(core,core.getConfiguration().getInt("spectator.ticksAllowed"));
                    tasks.put(player.getUniqueId(),task);

                }
                else
                {
                    player.sendMessage(strings.get("hurt"));
                }


            }
            return true;
        }
        else
        {
            return false;
        }
    }

    private class SpectatorTask extends BukkitRunnable
    {
        UUID playerUUID;

        public SpectatorTask(UUID playerUUID)
        {
            this.playerUUID = playerUUID;
        }

        @Override
        public void run()
        {
            if(this.isCancelled())
            {
                return;
            }

            Server server = Bukkit.getServer();
            Player player = server.getPlayer(playerUUID);

            if(player != null)
            {
                if(!(player.getGameMode() == GameMode.SPECTATOR))
                {
                    return;
                }

                if(locations.containsKey(player.getUniqueId()))
                {
                    player.teleport(locations.get(player.getUniqueId()));
                    locations.remove(player.getUniqueId());
                }
                player.setGameMode(GameMode.SURVIVAL);
                player.sendMessage(strings.get("spectatorRemoved"));
            }
        }
    }
}
