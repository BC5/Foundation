package uk.lsuth.mc.foundation.chat;

import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import uk.lsuth.mc.foundation.data.DataManager;
import uk.lsuth.mc.foundation.data.PlayerDataWrapper;

public class ChatManager implements Listener
{
    MessageBuilder builder;
    DataManager dmgr;

    public ChatManager(MessageBuilder builder, DataManager dmgr)
    {
        this.builder = builder;
        this.dmgr = dmgr;
    }

    @EventHandler
    public void chatEvent(AsyncPlayerChatEvent event)
    {
        String msg = builder.build(event.getPlayer(), event.getMessage());
        event.setFormat(msg);
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
