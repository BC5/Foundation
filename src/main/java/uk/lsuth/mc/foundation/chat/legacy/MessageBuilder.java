package uk.lsuth.mc.foundation.chat.legacy;

import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.regex.Matcher;

public class MessageBuilder
{

    Chat chat;
    Map<String,String> strings;

    public MessageBuilder(Map<String,String> strings)
    {
        this.strings = strings;
        if(Bukkit.getServer().getPluginManager().getPlugin("Vault") != null)
        {
            chat = Bukkit.getServer().getServicesManager().getRegistration(Chat.class).getProvider();
        }
    }

    public String buildJoinMessage(String player, String prefix)
    {
        String msg;
        msg = strings.get("join");
        msg = msg.replace("{player}",player);
        msg = msg.replace("{prefix}", prefix);
        return msg;
    }

    public static String formatCoordinate(Matcher m)
    {
        String format = "§2[§cX:{x}§2] [§aY:{y}§2] [§bZ:{z}§2]";

        //System.out.println("\""+m.group(1)+"\"");

        if(m.group("n") != null)
        {
            if(m.group("n").equals("nether "))
            {
                format = "§5[§cX:{x}§5] [§aY:{y}§5] [§bZ:{z}§5]";
            }
        }

        format = format.replace("{x}",m.group("x"));
        format = format.replace("{y}",m.group("y"));
        format = format.replace("{z}",m.group("z"));

        return format;
    }

    public static int[] getCoords(Matcher m)
    {
        int[] coords = new int[3];
        coords[0] = Integer.parseInt(m.group("x"));
        coords[1] = Integer.parseInt(m.group("y"));
        coords[2] = Integer.parseInt(m.group("z"));
        return coords;
    }

    public String buildJoinMessage(Player player)
    {
        String prefix = getPrefix(player);
        return buildJoinMessage(player.getDisplayName(),prefix);
    }

    public String buildQuitMessage(String player, String prefix)
    {
        String msg;
        msg = strings.get("leave");
        msg = msg.replace("{player}",player);
        msg = msg.replace("{prefix}", prefix);
        return msg;
    }

    public String buildQuitMessage(Player player)
    {
        String prefix = getPrefix(player);
        return buildQuitMessage(player.getDisplayName(),prefix);
    }

    private String getPrefix(Player player)
    {
        String prefix;
        if(chat != null)
        {
            prefix = chat.getPlayerPrefix(player);

            if(prefix == null)
            {
                prefix = "";
            }
        }
        else
        {
            prefix = "";
        }

        return prefix;
    }

    public String build(String player, String prefix, String message)
    {
        //Process message
        message = markdown(message);

        //Build string
        String msg = strings.get("format");
        msg = msg.replace("{prefix}",prefix);
        msg = msg.replace("{player}",player);
        msg = msg.replace("{message}",message);

        return msg;
    }

    public String build(Player player, String message)
    {
        String prefix = getPrefix(player);
        return build(player.getDisplayName(),prefix,message);
    }

    private String markdown(String input)
    {

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
        return input;
    }
}
