package uk.lsuth.mc.foundation.world;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.language.LanguageManager;

import java.util.ArrayList;
import java.util.Map;

public class ContainerQuery extends FoundationCommand
{

    FoundationCore core;
    Map<String,String> strings;
    LanguageManager lmgr;


    public ContainerQuery(FoundationCore core)
    {
        super("query");
        this.lmgr = core.getLmgr();
        strings = lmgr.getCommandStrings("query");
        this.core = core;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        int radius = 5;

        if(args.length == 2)
        {
            try
            {
                radius = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException e)
            {
                return false;
            }
        }

        Material queryFor = Material.getMaterial(args[0]);

        if(queryFor == null)
        {
            sender.sendMessage(strings.get("unknown"));
            return true;
        }

        if(sender instanceof Player)
        {
            long startTime = System.currentTimeMillis();

            Player player = (Player) sender;
            World w = player.getWorld();

            Block playerBlock = w.getBlockAt(player.getLocation());

            ArrayList<Inventory> matches = new ArrayList<>();

            //Get all containers with item
            for(int x = -radius; x <= radius; x++)
            {
                for(int z = -radius; z <= radius; z++)
                {
                    for(int y = -radius; y <= radius; y++)
                    {
                        Block b = playerBlock.getRelative(x,y,z);
                        if(b.getState() instanceof Container)
                        {
                            Container c = (Container) b.getState();

                            if(c.getInventory().contains(queryFor))
                            {
                                matches.add(c.getInventory());
                                //Highlight container
                                new HighlightBlockTask(c,6).runTaskTimer(core,0,10);
                            }
                        }
                    }
                }
            }

            int count = 0;


            for(Inventory inv:matches)
            {
                //Count the amount of items in the container
                for(ItemStack i : inv.getContents())
                {
                    if(i != null)
                    {
                        if(i.getType() == queryFor)
                        {
                            count = count + i.getAmount();
                        }
                    }
                }
            }

            if(matches.size() == 0)
            {
                sender.sendMessage(strings.get("none").replace("{x}",lmgr.getLocalisedName(queryFor)).replace("{y}",Integer.toString(radius)));
            }
            else
            {
                String msg = strings.get("response");

                long time = System.currentTimeMillis() - startTime;

                msg = msg.replace("{milliseconds}",Long.toString(time));
                msg = msg.replace("{radius}",Integer.toString(radius));
                msg = msg.replace("{item}",lmgr.getLocalisedName(queryFor));
                msg = msg.replace("{amount}",Integer.toString(count));

                sender.sendMessage(msg);
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    private static Particle.DustOptions data = new Particle.DustOptions(Color.FUCHSIA,1);

    private static void highlightContainer(Container c)
    {
        World w = c.getWorld();

        int intermediateParticles = 3;
        Location loc = c.getLocation();
        double x,y,z;
        x = loc.getBlockX();
        y = loc.getBlockY();
        z = loc.getBlockZ();

        drawSquare(w,x,y,z,intermediateParticles);
        drawSquare(w,x,y+1,z,intermediateParticles);
        drawPillars(w,x,y,z,intermediateParticles);

    }

    private static void drawSquare(World w,double x, double y, double z, int intermediate)
    {
        for(double i = 0; i <= 1; i = i+((intermediate+2)/1))
        {
            w.spawnParticle(Particle.REDSTONE,x+i,y,z,1,data);
        }

        for(double i = 0; i <= 1; i = i+((intermediate+2)/1))
        {
            w.spawnParticle(Particle.REDSTONE,x+i,y,z+1,1,data);
        }

        for(double i = 0; i <= 1; i = i+((intermediate+2)/1))
        {
            w.spawnParticle(Particle.REDSTONE,x,y,z+i,1,data);
        }

        for(double i = 0; i <= 1; i = i+((intermediate+2)/1))
        {
            w.spawnParticle(Particle.REDSTONE,x+1,y,z+i,1,data);
        }
    }

    private static void drawPillars(World w,double x, double y, double z, int intermediate)
    {
        for(double i = 0; i <= 1; i = i+((intermediate+2)/1))
        {
            w.spawnParticle(Particle.REDSTONE,x,y+i,z,1,data);
        }

        for(double i = 0; i <= 1; i = i+((intermediate+2)/1))
        {
            w.spawnParticle(Particle.REDSTONE,x,y+i,z+1,1,data);
        }

        for(double i = 0; i <= 1; i = i+((intermediate+2)/1))
        {
            w.spawnParticle(Particle.REDSTONE,x+1,y+i,z,1,data);
        }

        for(double i = 0; i <= 1; i = i+((intermediate+2)/1))
        {
            w.spawnParticle(Particle.REDSTONE,x+1,y+i,z+1,1,data);
        }
    }

    private class HighlightBlockTask extends BukkitRunnable
    {
        Container container;
        int runs;

        public HighlightBlockTask(Container c,int maxRuns)
        {
            this.container = c;
            this.runs = maxRuns;
        }

        @Override
        public void run()
        {
            highlightContainer(this.container);

            if(runs < 1)
            {
                this.cancel();
            }
            else
            {
                runs--;
            }
        }
    }
}
