package uk.lsuth.mc.foundation.chat;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.data.DataManager;
import uk.lsuth.mc.foundation.data.PlayerDataWrapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatManager implements Listener
{
    public final static String pattern = "(?<n>nether )?[xX]:(?<x>-?\\d+) [yY]:(?<y>-?\\d+) [zZ]:(?<z>-?\\d+)";

    public Pattern coordinatePattern;

    MessageBuilder builder;
    DataManager dmgr;

    public ChatManager(FoundationCore core)
    {
        builder = new MessageBuilder(core.getLmgr().getStrings("chat"));
        dmgr = core.getDmgr();
        coordinatePattern = Pattern.compile(pattern);
    }

    public ChatManager(MessageBuilder builder, DataManager dmgr)
    {
        this.builder = builder;
        this.dmgr = dmgr;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void chatEvent(AsyncPlayerChatEvent event)
    {
        Matcher m = coordinatePattern.matcher(event.getMessage());

        if(m.matches())
        {
            String msg = event.getMessage();
            msg = msg.replaceFirst(pattern,builder.formatCoordinate(m));
            msg = builder.build(event.getPlayer(),msg);

            int[] coordinates = builder.getCoords(m);
            String command = getCommand(m,coordinates,event.getPlayer().getDisplayName());



            TextComponent txt = new TextComponent(TextComponent.fromLegacyText(msg));
            event.setCancelled(true);
            ClickEvent ce = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,command);
            txt.setClickEvent(ce);

            Bukkit.getServer().broadcast(txt);
        }
        else
        {
            String msg = builder.build(event.getPlayer(), event.getMessage());
            event.setCancelled(true);
            Bukkit.getServer().broadcastMessage(msg);
        }


    }

    private static String getCommand(Matcher m, int[] coords, String uname)
    {
        String cmd = "/setmarker " + uname + "-CHAT ";
        cmd = cmd + coords[0] + " ";
        cmd = cmd + coords[1] + " ";
        cmd = cmd + coords[2];

        if(m.group("n") != null)
        {
            if (m.group("n").equals("nether "))
            {
                cmd = cmd + " " + Bukkit.getWorlds().get(1).getName();
            }
        }


        return cmd;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onLogin(PlayerJoinEvent e)
    {
        Player player = e.getPlayer();

        PlayerDataWrapper pdw = dmgr.fetchData(player);
        Document pdoc = pdw.getPlayerDocument();
        String nickname = pdoc.getString("nickname");
        if(nickname != null)
        {
            player.setDisplayName(nickname);
            player.setPlayerListName(nickname);
            player.setCustomName(nickname);

        }

        e.setJoinMessage(builder.buildJoinMessage(player));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onLeave(PlayerQuitEvent e)
    {
        e.setQuitMessage(builder.buildQuitMessage(e.getPlayer()));
    }

}
