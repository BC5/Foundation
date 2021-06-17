package uk.lsuth.mc.foundation.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import net.milkbowl.vault.chat.Chat;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.data.DataManager;
import uk.lsuth.mc.foundation.data.PlayerDataWrapper;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaperChatManager implements Listener
{

    Chat vaultChat;
    FoundationCore core;
    ChatRenderer renderer;
    DataManager dmgr;
    Map<String,String> msgs;

    public PaperChatManager(FoundationCore core)
    {
        this.core = core;
        this.dmgr = core.getDmgr();
        this.msgs = core.getLmgr().getStrings("chat");

        vaultChat = Bukkit.getServer().getServicesManager().getRegistration(Chat.class).getProvider();
        renderer = new ChatRenderer();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncChatEvent event)
    {
        event.renderer(renderer);
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent e)
    {
        Player player = e.getPlayer();
        renderer.setNames(player);
        e.joinMessage(renderer.joinMessage(player));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e)
    {
        e.quitMessage(renderer.leaveMessage(e.getPlayer()));
    }


    public class ChatRenderer implements io.papermc.paper.chat.ChatRenderer
    {
        private static final String mcColourRegex = "(§[0-9a-fA-F])";
        private static final String mcStyleRegex = "(§[l-o])";
        private static final String hexRegex = "((?:#[0-9a-fA-F]{6})|(?:#[0-9a-fA-F]{3}))";
        private static final String boldRegex = "(\\*\\*[^\\*]+\\*\\*)";
        private static final String italicsRegex = "(\\*[^\\*]+\\*)";

        Pattern colourPattern;
        Pattern stylePattern;
        Pattern hexPattern;
        Pattern boldPattern;
        Pattern italicsPattern;

        Component chatSeparator;
        Component leave;
        Component join;

        public ChatRenderer()
        {
            colourPattern = Pattern.compile(mcColourRegex);
            hexPattern = Pattern.compile(hexRegex);
            stylePattern = Pattern.compile(mcStyleRegex);
            boldPattern = Pattern.compile(boldRegex);
            italicsPattern = Pattern.compile(italicsRegex);

            chatSeparator = Component.text(msgs.get("separator"),NamedTextColor.YELLOW);
            leave = Component.text(msgs.get("leave"),NamedTextColor.GOLD);
            join = Component.text(msgs.get("join"),NamedTextColor.GOLD);
        }

        public void setNames(Player p)
        {
            PlayerDataWrapper pdw = dmgr.fetchData(p);
            Document pdoc = pdw.getPlayerDocument();
            String nickname = pdoc.getString("nickname");

            String name = p.getName();
            if(nickname != null)
            {
                name = nickname;
            }

            Component namec = Component.text(name);
            namec = namec.style(getPlayerStyle(p));
            p.displayName(namec);
            p.playerListName(namec);
            p.customName(namec);
        }

        public Component joinMessage(Player p)
        {
            return p.displayName().append(join);
        }

        public Component leaveMessage(Player p)
        {
            return p.displayName().append(leave);
        }

        @Override
        public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer)
        {
            TextComponent finalMessage = Component.text("");

            //PLAYER NAME
            Component name = sourceDisplayName;
            //name = name.style(getPlayerStyle(source));
            finalMessage = finalMessage.append(name);

            //SEPARATOR
            finalMessage = finalMessage.append(chatSeparator);

            //MESSAGE
            finalMessage = finalMessage.append(buildMessage(message));

            core.log.info(PlainComponentSerializer.plain().serialize(finalMessage));

            return finalMessage;
        }

        private Component buildMessage(Component message)
        {
            message = markdown(message);
            message = mentions(message);
            return message;
        }

        private Style getPlayerStyle(Player p)
        {
            Style pStyle = Style.style(NamedTextColor.WHITE);

            if(vaultChat != null)
            {
                String prefix = vaultChat.getPlayerPrefix(p);
                Matcher cpm = colourPattern.matcher(prefix);
                Matcher spm = stylePattern.matcher(prefix);
                Matcher hpm = hexPattern.matcher(prefix);

                //Apply Colour
                if(cpm.find())
                {
                    pStyle = pStyle.color(fromLegacyColour(cpm.group()));
                }

                //Apply finer colour
                if(hpm.find())
                {
                    pStyle = pStyle.color(TextColor.fromCSSHexString(hpm.group()));
                }

                //Apply text decoration
                while(spm.find())
                {
                    pStyle = pStyle.decorate(fromLegacyDecoration(spm.group()));
                }
            }

            return pStyle;
        }

        private Component mentions(Component component)
        {
            LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection();
            String message = serializer.serialize(component);

            int i = message.indexOf("@");

            while(i != -1)
            {
                int j = message.indexOf(" ",i);

                String s;

                if(j != -1)
                {
                    s = message.substring(i+1,j);
                }
                else
                {
                    s = message.substring(i+1);
                }

                Player p = Bukkit.getServer().getPlayer(s);

                if(p != null)
                {
                    Bukkit.getScheduler().runTask(core,()->
                    {
                        p.playSound(Sound.sound(Key.key("block.note_block.bell"),Sound.Source.MASTER, 1f, 0.8f));
                    });


                    //Add pretty colours
                    Component newComp = Component.text("@"+s);
                    newComp = newComp.style(getPlayerStyle(p));

                    if(newComp.color().compareTo(NamedTextColor.WHITE) == 0)
                    {
                        newComp = newComp.color(NamedTextColor.DARK_AQUA);
                    }

                    TextReplacementConfig.Builder b = TextReplacementConfig.builder();
                    b = b.matchLiteral("@"+s);
                    //b = b.once();
                    b = b.replacement(newComp);

                    component = component.replaceText(b.build());

                }

                i = message.indexOf("@", i+1);
            }

            return component;
        }

        private static Component markdown(Component compin)
        {
            LegacyComponentSerializer serialiser = LegacyComponentSerializer.legacySection();

            String input = serialiser.serialize(compin);

            while(input.contains("**"))
            {
                //Open bold
                input = input.replaceFirst("\\*\\*","§l");
                //Close bold
                input = input.replaceFirst("\\*\\*","§r");
            }

            while(input.contains("*"))
            {
                //Open italic
                input = input.replaceFirst("\\*","§o");
                //Close italic
                input = input.replaceFirst("\\*","§r");
            }

            return serialiser.deserialize(input);
        }

        private static TextDecoration fromLegacyDecoration(String s)
        {
            switch (s)
            {
                case "§l": return TextDecoration.BOLD;
                case "§m": return TextDecoration.STRIKETHROUGH;
                case "§n": return TextDecoration.UNDERLINED;
                case "§o": return TextDecoration.ITALIC;
                default: return null;
            }
        }

        private static NamedTextColor fromLegacyColour(String s)
        {
            switch (s)
            {
                case "§0": return NamedTextColor.BLACK;
                case "§1": return NamedTextColor.DARK_BLUE;
                case "§2": return NamedTextColor.DARK_GREEN;
                case "§3": return NamedTextColor.DARK_AQUA;
                case "§4": return NamedTextColor.DARK_RED;
                case "§5": return NamedTextColor.DARK_PURPLE;
                case "§6": return NamedTextColor.GOLD;
                case "§7": return NamedTextColor.GRAY;
                case "§8": return NamedTextColor.DARK_GRAY;
                case "§9": return NamedTextColor.BLUE;
                case "§a": return NamedTextColor.GREEN;
                case "§b": return NamedTextColor.AQUA;
                case "§c": return NamedTextColor.RED;
                case "§d": return NamedTextColor.LIGHT_PURPLE;
                case "§e": return NamedTextColor.YELLOW;
                case "§f": return NamedTextColor.WHITE;
                default: return null;
            }
        }
    }



}
