package uk.lsuth.mc.foundation.essentialcommands;

import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import uk.lsuth.mc.foundation.FoundationCommand;

public class Ping extends FoundationCommand
{
    Plugin plugin;

    public Ping(Plugin plugin)
    {
        super("ping");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args)
    {
        switch(args.length)
        {
            //Player, no message
            case 1:
                Player x = commandSender.getServer().getPlayer(args[0]);
                x.playNote(x.getLocation(), Instrument.BIT, Note.sharp(1, Note.Tone.D));
                x.sendMessage("You have been pinged by " + commandSender.getName());
                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        x.playNote(x.getLocation(), Instrument.BIT, Note.sharp(1, Note.Tone.F));
                    }
                }.runTaskLater(this.plugin,2);
                return true;
        }

        return false;
    }
}
