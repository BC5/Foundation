package uk.lsuth.mc.foundation.world;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class Bind extends FoundationCommand implements Listener
{

    NamespacedKey bindKey;
    Map<String,String> strings;
    Logger log;
    FoundationCore core;

    public Bind(FoundationCore core)
    {
        super("bind");
        this.core = core;
        log = core.log;
        bindKey = new NamespacedKey(core,"bound");
        strings = core.getLmgr().getCommandStrings("bind");
    }

    @EventHandler
    public void onEnderPearl(ProjectileLaunchEvent e)
    {
        if(e.getEntityType() == EntityType.ENDER_PEARL)
        {
            EnderPearl ep = (EnderPearl) e.getEntity();
            if(ep.getItem().getItemMeta().getPersistentDataContainer().has(bindKey,PersistentDataType.STRING))
            {
                Player target = Bukkit.getPlayer(UUID.fromString(ep.getItem().getItemMeta().getPersistentDataContainer().get(bindKey,PersistentDataType.STRING)));

                if(target == null)
                {
                    e.setCancelled(true);
                }
                else
                {
                    ep.setShooter(target);
                }

            }
        }

        if(e.getEntityType() == EntityType.ARROW)
        {
            Arrow arrow = (Arrow) e.getEntity();
            if(arrow.getShooter() instanceof Player p)
            {
                ItemStack bow = p.getInventory().getItemInMainHand();
                if(bow.getType() != Material.BOW) return;
                if(bow.getItemMeta().getPersistentDataContainer().has(bindKey,PersistentDataType.STRING))
                {
                    Player target = Bukkit.getPlayer(UUID.fromString(bow.getItemMeta().getPersistentDataContainer().get(bindKey,PersistentDataType.STRING)));
                    if(target == null) return;
                    if(target.getWorld() != p.getWorld()) return;

                    arrow.setGravity(false);
                    Location start = arrow.getLocation();
                    Location end = target.getLocation().add(0,1,0);
                    Vector targetVelocity = start.subtract(end).toVector();

                    //Metres per tick
                    float targetspeed = 3f;

                    double speeddiff = targetspeed / targetVelocity.length();
                    targetVelocity = targetVelocity.multiply(speeddiff);
                    log.info(targetVelocity.toString());

                    new ArrowTask(arrow,targetVelocity).runTaskTimer(core,1,1);
                }
            }



        }

    }

    private class ArrowTask extends BukkitRunnable
    {
        Arrow arrow;
        Vector target;

        int ticks = 0;

        ArrowTask(Arrow arrow, Vector target)
        {
            this.arrow = arrow;
            this.target = target;
        }

        @Override
        public void run()
        {
            if(ticks > 100) this.cancel();
            if(arrow == null) this.cancel();
            if(arrow.isDead()) this.cancel();
            if(arrow.isInBlock()) this.cancel();
            Vector cur = arrow.getVelocity();
            if(cur.length() == 0)
            {
                this.cancel();
                return;
            }
            Vector diff = cur.subtract(target);
            diff = diff.divide(new Vector(4,4,4));
            arrow.setVelocity(cur.add(diff));
            log.info(cur.toString());
            ticks++;
        }

        @Override
        public synchronized void cancel()
        {
            Bukkit.getScheduler().runTask(core,()->{arrow.setGravity(true);});
            super.cancel();
        }

    }

    @EventHandler
    public void onEnderEye(EntitySpawnEvent e)
    {
        if(e.getEntityType() == EntityType.ENDER_SIGNAL)
        {
            log.info("ENDERSIGNAL");
            EnderSignal es = (EnderSignal) e.getEntity();
            if(es.getItem().getItemMeta().getPersistentDataContainer().has(bindKey,PersistentDataType.STRING))
            {
                Player target = Bukkit.getPlayer(UUID.fromString(es.getItem().getItemMeta().getPersistentDataContainer().get(bindKey,PersistentDataType.STRING)));

                if(target == null)
                {
                    e.setCancelled(true);
                }
                else
                {
                    es.setTargetLocation(target.getLocation());
                }
            }
        }
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if(sender instanceof Player p)
        {

            if(!p.hasPermission("foundation.bind"))
            {
                sender.sendMessage(FoundationCore.noPermission);
                return true;
            }

            OfflinePlayer t = p;

            Component displayname = p.displayName();
            if(args.length == 1)
            {
                if(!p.hasPermission("foundation.bind.others"))
                {
                    sender.sendMessage(FoundationCore.noPermission);
                    return true;
                }

                //noinspection deprecation
                t = Bukkit.getOfflinePlayer(args[0]);
                if(t == null)
                {
                    sender.sendMessage(strings.get("noplayer"));
                    return true;
                }


                if(t.isOnline())
                {
                    displayname = t.getPlayer().displayName();
                }
                else
                {
                    displayname = Component.text(t.getName());
                }

            }

            ItemStack x = p.getInventory().getItemInMainHand();

            if(x.getType() == Material.ENDER_PEARL || x.getType() == Material.ENDER_EYE || x.getType() == Material.BOW)
            {
                ItemMeta meta = x.getItemMeta();
                PersistentDataContainer pdc = meta.getPersistentDataContainer();
                pdc.set(bindKey, PersistentDataType.STRING,t.getUniqueId().toString());
                ArrayList<Component> lore = new ArrayList<>();
                lore.add(Component.text(strings.get("bindlore"), NamedTextColor.LIGHT_PURPLE).append(displayname));
                meta.lore(lore);
                if(x.getType() == Material.BOW)
                {
                    meta.displayName(Component.text(strings.get("bowname"), NamedTextColor.LIGHT_PURPLE));
                    meta.addEnchant(Enchantment.BINDING_CURSE,1,false);
                }
                x.setItemMeta(meta);
                p.sendMessage(Component.text(strings.get("bound"),NamedTextColor.LIGHT_PURPLE).append(displayname));
            }

        }
        return true;
    }
}
