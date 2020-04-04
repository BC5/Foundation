package uk.lsuth.mc.foundation.essentialcommands;

import org.bson.Document;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.data.PlayerDataWrapper;

import java.util.List;
import java.util.Map;

public class MailListener implements Listener
{
    FoundationCore core;
    String notify;

    public MailListener(FoundationCore core)
    {
        this.core = core;
        notify = core.getLmgr().getCommandStrings("mail").get("notify");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e)
    {
        PlayerDataWrapper pdw = core.dmgr.fetchData(e.getPlayer());
        Document playerDocument = pdw.getPlayerDocument();
        List<Map<String,String>> mailItems = (List<Map<String,String>>) playerDocument.get("mailbox");

        if(mailItems == null)
        {
            return;
        }

        if(mailItems.size() > 0)
        {
            e.getPlayer().sendMessage(notify);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e)
    {
        String title = e.getWhoClicked().getOpenInventory().getTitle();
        String mailTitle = core.getLmgr().getCommandStrings("mail").get("guiMailbox");

        //Prevent spamming console with exceptions every time somebody clicks out of bounds
        if(e.getClickedInventory() == null)
        {
            return;
        }

        if(title.equals(mailTitle) && e.getClickedInventory().getType() == InventoryType.CHEST)
        {
            e.setCancelled(true);
            ItemStack is = e.getCurrentItem();

            if(e.getAction() != InventoryAction.PICKUP_ALL)
            {
                return;
            }

            if(e.getWhoClicked() instanceof Player && is != null)
            {
                Player player = (Player) e.getWhoClicked();
                PlayerDataWrapper playerDataWrapper = core.dmgr.fetchData(player);
                Document pdoc = playerDataWrapper.getPlayerDocument();

                ItemMeta isMeta = is.getItemMeta();
                String itemUUID = isMeta.getPersistentDataContainer().get(Mail.deliveryIDKey, PersistentDataType.STRING);

                List<Map<String,String>> mailBox = (List<Map<String,String>>) pdoc.get("mailbox");

                Map<String,String> dbItem = null;

                for(Map<String,String> map:mailBox)
                {
                    String dbUUID = map.get("deliveryUUID");
                    if(dbUUID.equals(itemUUID))
                    {
                        dbItem = map;
                        break;
                    }
                }

                if(dbItem != null)
                {
                    //Remove from Database
                    mailBox.remove(dbItem);
                    pdoc.put("mailbox",mailBox);

                    //Remove from mailbox gui
                    e.getClickedInventory().remove(is);

                    try
                    {
                        //Deserialise
                        YamlConfiguration itemcfg = new YamlConfiguration();
                        itemcfg.loadFromString(dbItem.get("itemStack"));
                        ItemStack nis = itemcfg.getItemStack("item");

                        //Add to player's inventory
                        player.getInventory().addItem(nis);
                    }
                    catch (InvalidConfigurationException err)
                    {
                        core.log.severe("Foundation couldn't deserialise ItemStack");
                        err.printStackTrace();
                    }

                }
            }

        }
        else if(title.equals(mailTitle))
        {
            if(e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY)
            {
                e.setCancelled(true);
            }
        }
    }
}
