package uk.lsuth.mc.foundation.chat;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatManager implements Listener
{
    MessageBuilder builder;

    public ChatManager(MessageBuilder builder)
    {
        this.builder = builder;
    }

    @EventHandler
    public void chatEvent(AsyncPlayerChatEvent event)
    {
        String msg = builder.build(event.getPlayer().getDisplayName(), event.getMessage());
        event.setFormat(msg);
    }

}
