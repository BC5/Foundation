package uk.lsuth.mc.foundation.essentialcommands;

import net.milkbowl.vault.economy.Economy;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.data.PlayerDataWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Mail extends FoundationCommand
{
    FoundationCore core;
    Map<String,String> strings;
    protected static NamespacedKey deliveryIDKey;

    Economy eco;

    public Mail(FoundationCore core, Economy eco)
    {
        super("mail");
        this.core = core;
        this.eco = eco;
        strings = core.getLmgr().getCommandStrings(this.getCommand());
        deliveryIDKey = new NamespacedKey(core,"delivery-id");
        this.completer = new MailTabComplete();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(sender instanceof Player)
        {
            Player mailSender = (Player) sender;

            if(args.length == 2 || args.length == 3)
            {
                if(args[0].equals("send"))
                {
                    if(!(mailSender.hasPermission("foundation.mail.send")))
                    {
                        mailSender.sendMessage(FoundationCore.noPermission);
                        return true;
                    }

                    Document recipientDocument;

                    //Try to get online player
                    OfflinePlayer recipient = Bukkit.getServer().getPlayer(args[1]);
                    boolean offline = false;

                    if(recipient == null)
                    {
                         //Getting online player failed. Try and load database playerdoc. Avoiding deprecated getOfflinePlayer(String)
                         PlayerDataWrapper pdw = core.dmgr.fetchData(args[1]);
                         if(pdw == null)
                         {
                             sender.sendMessage(strings.get("noPlayer").replace("{x}",args[1]));
                             return true;
                         }
                         else
                         {
                             offline = true;
                             recipient = pdw.getPlayer();
                             recipientDocument = pdw.getPlayerDocument();
                         }
                    }
                    else
                    {
                        recipientDocument = core.dmgr.fetchData(recipient).getPlayerDocument();
                    }

                    //Get player's hand
                    ItemStack playerHand = mailSender.getInventory().getItemInMainHand();
                    ItemStack toSend = playerHand;

                    if(args.length == 3)
                    {
                        int amnt;

                        try
                        {
                            if (args[2].charAt(args[2].length() - 1) == 's')
                            {
                                amnt = Integer.parseInt(args[2].substring(0, args[2].length() - 2));
                            }
                            else
                            {
                                amnt = Integer.parseInt(args[2]);
                            }
                        }
                        catch (NumberFormatException e)
                        {
                            core.log.warning("Invalid amount: " + args[2]);
                            return false;
                        }

                        if(toSend.getAmount() >= amnt)
                        {
                            ItemStack newStack = toSend.clone();
                            newStack.setAmount(amnt);
                            toSend = newStack;
                        }
                        else
                        {
                            sender.sendMessage(strings.get("notEnough"));
                            return true;
                        }
                    }

                    if(toSend == null)
                    {
                        sender.sendMessage(strings.get("hand"));
                        if(offline)
                        {
                            core.dmgr.unloadPlayer(recipient);
                        }
                        return true;
                    }
                    else if(toSend.getType() == Material.AIR)
                    {
                        sender.sendMessage(strings.get("hand"));
                        if(offline)
                        {
                            core.dmgr.unloadPlayer(recipient);
                        }
                        return true;
                    }
                    else
                    {
                        int fee = 0;
                        if(toSend.getType() == Material.SHULKER_BOX)
                        {
                            fee = core.getConfiguration().getInt("mail.shulkerFee");
                            if(!eco.has(mailSender,fee))
                            {
                                mailSender.sendMessage(strings.get("insufficientFunds").replace("{x}",Integer.toString(fee)));
                                return true;
                            }
                        }

                        List<Document> mailbox = (List<Document>) recipientDocument.get("mailbox");

                        if(mailbox == null)
                        {
                            mailbox = new ArrayList<Document>();
                        }
                        else if(mailbox.size() >= 9)
                        {
                            sender.sendMessage(strings.get("recipientFull").replace("{x}",args[1]));
                            if(offline)
                            {
                                core.dmgr.unloadPlayer(recipient);
                            }
                            return true;
                        }

                        Document mailItem = new Document();


                        YamlConfiguration yml = new YamlConfiguration();
                        yml.set("item",toSend);
                        String itemString = yml.saveToString();


                        mailItem.put("itemStack",itemString);
                        mailItem.put("senderUUID",mailSender.getUniqueId().toString());
                        mailItem.put("senderName",mailSender.getName());
                        mailItem.put("deliveryUUID", UUID.randomUUID().toString());

                        //Update mailbox
                        mailbox.add(mailItem);
                        recipientDocument.put("mailbox",mailbox);



                        String msg = strings.get("sent");

                        msg = msg.replace("{w}",toSend.getAmount() + "");
                        msg = msg.replace("{x}",core.getLmgr().getLocalisedName(toSend,true));
                        msg = msg.replace("{y}",args[1]);
                        msg = msg.replace("{z}",eco.format(fee));

                        mailSender.sendMessage(msg);
                        if(recipient instanceof Player)
                        {
                            ((Player) recipient).sendMessage(strings.get("notify"));
                        }

                        //Remove from initial inventory
                        if(toSend == playerHand)
                        {
                            mailSender.getInventory().removeItem(playerHand);
                        }
                        else
                        {
                            playerHand.setAmount(playerHand.getAmount() - toSend.getAmount());
                        }

                        //Charge fee
                        if(fee != 0)
                        {
                            eco.withdrawPlayer(mailSender,fee);
                        }

                        //If offlineplayer, unload
                        if(offline)
                        {
                            core.dmgr.unloadPlayer(recipient);
                        }
                        return true;
                    }

                }
            }
            else if(args.length == 1)
            {
                if(args[0].equals("get"))
                {
                    if(!(mailSender.hasPermission("foundation.mail.get")))
                    {
                        mailSender.sendMessage(FoundationCore.noPermission);
                        return true;
                    }

                    Inventory inv = Bukkit.createInventory(null,9, strings.get("guiMailbox"));
                    PlayerDataWrapper pdw = core.dmgr.fetchData(mailSender);
                    Document playerDocument = pdw.getPlayerDocument();
                    List<Map<String,String>> mailItems = (List<Map<String,String>>) playerDocument.get("mailbox");

                    //If it's nonexistent, assume blank
                    if(mailItems == null)
                    {
                        mailSender.openInventory(inv);
                        return true;
                    }

                    YamlConfiguration itemcfg = new YamlConfiguration();
                    for(Map<String,String> map:mailItems)
                    {
                        try
                        {
                            itemcfg.loadFromString(map.get("itemStack"));
                            ItemStack is = itemcfg.getItemStack("item");

                            ItemMeta meta = is.getItemMeta();

                            ArrayList lore = new ArrayList<String>();
                            lore.add(strings.get("sender") + map.get("senderName"));

                            meta.getPersistentDataContainer().set(deliveryIDKey, PersistentDataType.STRING, map.get("deliveryUUID"));

                            meta.setLore(lore);
                            is.setItemMeta(meta);
                            inv.addItem(is);
                        }
                        catch (InvalidConfigurationException e)
                        {
                            core.log.severe("Foundation couldn't deserialise ItemStack");
                            e.printStackTrace();
                        }
                    }
                    mailSender.openInventory(inv);
                    return true;
                }
            }
            else
            {
                return false;
            }
        }
        return false;
    }

    static class MailTabComplete implements TabCompleter
    {
        List<String> commands;

        public MailTabComplete()
        {
            commands = new ArrayList<String>();
            commands.add("get");
            commands.add("send");
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
        {
            if(args.length == 1)
            {
                return commands;
            }
            else
            {
                return null;
            }
        }
    }

}
