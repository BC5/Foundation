package uk.lsuth.mc.foundation.management;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class Freeze extends FoundationCommand implements Listener
{
    private Map<String,String> strings;
    ArrayList<UUID> freezeList;
    FoundationCore core;

    public Freeze(FoundationCore core)
    {
        super("freeze");
        strings = core.getLmgr().getCommandStrings("freeze");
        freezeList = new ArrayList<UUID>();
        this.core = core;

    }


    @EventHandler
    public void playerMove(PlayerMoveEvent e)
    {
        if(isFrozen(e.getPlayer()))
        {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent e)
    {
        if(isFrozen(e.getPlayer()))
        {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent e)
    {
        if(isFrozen(e.getPlayer()))
        {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent e)
    {
        if(isFrozen(e.getPlayer()))
        {
            e.setCancelled(true);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(!(sender.hasPermission("foundation.management.freeze")))
        {
            sender.sendMessage(FoundationCore.noPermission);
            return true;
        }


        if(args.length == 1)
        {
            Player p = Bukkit.getPlayer(args[0]);
            if(p == null) return false;

            if(isFrozen(p))
            {
                unfreeze(p);
                sender.sendMessage(strings.get("unfreeze").replace("{x}",p.getDisplayName()));
            }
            else
            {
                freeze(p);
                sender.sendMessage(strings.get("freeze").replace("{x}",p.getDisplayName()));
            }
            return true;
        }
        return false;
    }


    public void freeze(Player p)
    {
        freezeList.add(p.getUniqueId());

        new BukkitRunnable()
        {

            final int animationFrames = 10;
            final double radius = 1.0;

            int count = 0;
            boolean invert = false;

            @Override
            public void run()
            {
                if(!isFrozen(p))
                {
                    this.cancel();
                }
                else
                {
                    if(p == null)
                    {
                        return;
                    }

                    Location loc = p.getLocation();

                    if(count >= animationFrames)
                    {
                        invert = true;
                    }

                    if(count <= 0)
                    {
                        invert = false;
                    }

                    double x = (0-radius) + ((1.0/(animationFrames/2)) * count);

                    if(invert)
                    {
                        count--;
                    }
                    else
                    {
                        count++;
                    }

                    double z;

                    if(invert)
                    {
                        z = Math.sqrt((radius*radius)-x*x);
                    }
                    else
                    {
                        z = 0-Math.sqrt((radius*radius)-x*x);
                    }

                    //System.out.println("[c:" + count + "][x:" + x + "][z:" + z + "]");
                    loc = loc.add(x,1,z);

                    p.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE,loc,3,0.05,0.05,0.05,0);
                }
            }
        }.runTaskTimer(core,0,1);

    }

    public void unfreeze(Player p)
    {
        UUID remove = null;
        for(UUID x:freezeList)
        {
            if(p.getUniqueId().equals(x))
            {
                remove = x;
            }
        }
        if(remove != null)
        {
            freezeList.remove(remove);
        }
    }

    public boolean isFrozen(Player p)
    {
        for(UUID u:freezeList)
        {
            if(p.getUniqueId().equals(u)) return true;
        }
        return false;
    }
}
