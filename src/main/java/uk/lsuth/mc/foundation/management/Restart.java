package uk.lsuth.mc.foundation.management;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;

public class Restart extends FoundationCommand
{
    FoundationCore core;

    public Restart(FoundationCore core)
    {
        super("restart");
        this.core = core;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(!(sender.hasPermission("foundation.management.restart")))
        {
            sender.sendMessage(FoundationCore.noPermission);
            return true;
        }

        if(args.length > 1)
        {
            return false;
        }

        int secondsUntilRestart = 5;

        if(args.length == 1)
        {
            try
            {
                secondsUntilRestart = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException e) { return false; };
            if(secondsUntilRestart == 0)
            {
                Bukkit.getServer().shutdown();
            }
        }

        Server server = Bukkit.getServer();

        BossBar bar = server.createBossBar(core.getLmgr().getCommandStrings("restart").get("bar"), BarColor.RED, BarStyle.SOLID);

        bar.setProgress(0);

        for(Player p:server.getOnlinePlayers())
        {
            bar.addPlayer(p);
        }

        int finalSecondsUntilRestart = secondsUntilRestart;

        new BukkitRunnable()
        {
            double runs = 0;
            final double maxRuns = finalSecondsUntilRestart*20;

            @Override
            public void run()
            {
                bar.setProgress(runs/maxRuns);
                if(runs >= maxRuns)
                {
                    server.shutdown();
                }
                runs++;
            }

        }.runTaskTimer(core,0,1);
        return true;

    }
}
