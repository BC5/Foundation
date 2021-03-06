package uk.lsuth.mc.foundation.gamerules;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bson.Document;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.data.DataManager;
import uk.lsuth.mc.foundation.data.PlayerDataWrapper;

import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;

public class Hardcore implements Listener
{
    //If bans are permanent or temporary
    boolean permban = false;

    //If to send signal to client that server is hardcore, changing heart graphics
    boolean doHearts = true;

    //Amount of time to ban after death
    int timeout = 30;

    //Amount of starting lives
    int startingLives = 3;

    //If killing another player adds a life
    boolean livestheft = false;

    //Time for a regeneration
    int regen = 30 * 60;

    DataManager dmgr;
    Logger log;
    FoundationCore core;

    static RegenTask tsk;

    public Hardcore(FoundationCore core)
    {
        this.core = core;
        dmgr = core.getDmgr();
        log = core.log;
        loadConfig(core.getConfiguration());
        if(doHearts) ProtocolLibrary.getProtocolManager().addPacketListener(new HardcoreHearts(core,core.log));
        if(regen > 1)
        {
            long delay = regen*20;
            long period = regen*20;

            if(tsk != null)
            {
                tsk.cancel();
                tsk = null;
            }

            tsk = new RegenTask();
            tsk.runTaskTimer(core,delay,period);
        }
    }

    private void loadConfig(Configuration cfg)
    {
        doHearts = cfg.getBoolean("hardcore.hearts");
        permban = cfg.getBoolean("hardcore.permban");
        timeout = cfg.getInt("hardcore.banlength");
        startingLives = cfg.getInt("hardcore.lives");
        livestheft = cfg.getBoolean("hardcore.livestheft");
        regen = cfg.getInt("regentime");
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e)
    {
        PlayerDataWrapper pdw = dmgr.fetchData(e.getEntity());
        String banmessage = e.getDeathMessage().replaceFirst(e.getEntity().getName(),"You");

        Document pdoc = pdw.getPlayerDocument();
        if(pdoc.get("hardcore") == null)
        {
            log.info("Assigning lives for first time to " + e.getEntity().getName());
            Document hardcore = new Document();
            hardcore.put("lives",startingLives);
            pdoc.put("hardcore",hardcore);
        }
        Document hardcore = (Document) pdoc.get("hardcore");

        //Subtract life lost
        int curLives = (int) hardcore.get("lives");
        curLives--;
        hardcore.put("lives",curLives);
        pdoc.put("hardcore",hardcore);

        log.info(e.getEntity().getName() + " now has " + curLives + " lives");

        LivingEntity killer = e.getEntity().getKiller();
        if(livestheft)
        {
            if(killer instanceof Player)
            {
                PlayerDataWrapper pdw2 = dmgr.fetchData((OfflinePlayer) killer);
                Document pdoc2 = pdw2.getPlayerDocument();
                if(pdoc2.get("hardcore") == null)
                {
                    Document hardcore2 = new Document();
                    hardcore2.put("lives",startingLives);

                    pdoc2.put("hardcore",hardcore2);
                }
                Document hardcore2 = (Document) pdoc2.get("hardcore");
                int x = (int) hardcore2.get("lives");
                hardcore2.put("lives",x+1);
                pdoc2.put("hardcore",hardcore2);
            }



        }

        if(!(killer instanceof Player))
        {
            e.getDrops().clear();
        }


        if(curLives == 0)
        {
            if(permban)
            {
                Bukkit.getBanList(BanList.Type.NAME).addBan(pdw.getPlayer().getName(),banmessage,null,null);
            }
            else
            {
                hardcore.put("lives",1);
                pdoc.put("hardcore",hardcore);
                Date date = new Date(System.currentTimeMillis()+timeout*1000);
                Bukkit.getBanList(BanList.Type.NAME).addBan(pdw.getPlayer().getName(),banmessage,date,null);
            }

            //Prevents IllegalStateException: Removing entity while ticking
            Bukkit.getScheduler().runTask(core, () -> e.getEntity().kickPlayer(banmessage));

        }


    }

    private class HardcoreHearts extends PacketAdapter
    {

        public HardcoreHearts(Plugin p, Logger log)
        {
            super(p, ListenerPriority.NORMAL, PacketType.Play.Server.LOGIN);
        }

        @Override
        public void onPacketReceiving(PacketEvent e){return;};

        @Override
        public void onPacketSending(PacketEvent event)
        {
            PacketContainer p = event.getPacket();
            p.getBooleans().write(0,true);
        }
    }

    class RegenTask extends BukkitRunnable
    {

        @Override
        public void run()
        {
            Collection<? extends Player> players = Bukkit.getOnlinePlayers();

            for(Player p : players)
            {
                int lives = HardcoreLivesCommand.getLives(p,dmgr);

                if(lives == startingLives || lives == 0)
                {

                }
                else if(lives < startingLives)
                {
                    PlayerDataWrapper pdw = dmgr.fetchData(p);
                    Document d = pdw.getPlayerDocument();
                    Document hc = (Document) d.get("hardcore");
                    hc.put("lives",lives+1);
                    d.put("hardcore",hc);
                    p.sendActionBar("§aYou now have " + lives+1 + " lives");
                    log.fine("Given a life to " + p.getName());
                }
            }

        }
    }

}
