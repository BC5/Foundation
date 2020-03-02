package uk.lsuth.mc.foundation.chat;

public class MessageBuilder
{

    String format;

    public MessageBuilder(String formatString)
    {
        this.format = formatString;
    }

    public String build(String player, String rank, String message)
    {
        //Process message
        message = markdown(message);

        //Build string
        String msg = format;
        msg = msg.replace("{rank}",rank);
        msg = msg.replace("{player}",player);
        msg = msg.replace("{message}",message);

        return msg;
    }

    public String build(String player, String message)
    {
        return build(player,"",message);
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
