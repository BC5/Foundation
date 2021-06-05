package uk.lsuth.mc.foundation.essentialcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;

import java.util.Map;

public class Moon extends FoundationCommand
{
    Map<String,String> strings;

    public Moon(FoundationCore core)
    {
        super("moon");
        strings = core.getLmgr().getCommandStrings("moon");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if(sender.hasPermission("foundation.moon"))
        {
            long fulltime;
            if(sender instanceof Player)
            {
                fulltime = ((Player) sender).getWorld().getFullTime();
            }
            else
            {
                fulltime = Bukkit.getWorlds().get(0).getFullTime();
            }

            sender.sendMessage(strings.get(MoonPhase.getMoonPhase(fulltime).toString()));
        }
        else
        {
            sender.sendMessage(FoundationCore.noPermission);
        }
        return true;
    }


    public enum MoonPhase
    {
        FULL (0),
        WANING_GIBBOUS (1),
        LAST_QUARTER (2),
        WANING_CRESCENT (3),
        NEW (4),
        WAXING_CRESCENT (5),
        FIRST_QUARTER (6),
        WAXING_GIBBOUS (7);

        final int num;
        MoonPhase(int num)
        {
            this.num = num;
        }

        public static MoonPhase getMoonPhase(long fulltime)
        {
            int phase = (int) (fulltime/24000l)%8;
            switch (phase)
            {
                case 0: return FULL;
                case 1: return WANING_GIBBOUS;
                case 2: return LAST_QUARTER;
                case 3: return WANING_CRESCENT;
                case 4: return NEW;
                case 5: return WAXING_CRESCENT;
                case 6: return FIRST_QUARTER;
                case 7: return WAXING_GIBBOUS;
                default: return null;
            }
        }

    }
}
