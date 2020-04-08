package uk.lsuth.mc.foundation.pvp;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Map;

public class PVPListener implements Listener
{
    Map<String,String> strings;
    FileConfiguration cfg;

    public PVPListener(Map<String,String> strings, FileConfiguration cfg)
    {
        this.strings = strings;
        this.cfg = cfg;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e)
    {
        Player victim = e.getEntity();

        if(victim.getKiller() != null)
        {
            Player murderer = victim.getKiller();

            if(cfg.getBoolean("pvp.beheading"))
            {
                //Create Playerhead
                ItemStack victimHead = new ItemStack(Material.PLAYER_HEAD, 1);
                SkullMeta meta = (SkullMeta) victimHead.getItemMeta();
                meta.setOwningPlayer(victim);

                //Update lore
                ArrayList<String> lore = new ArrayList<String>();
                lore.add(strings.get("headLore").replace("{x}", murderer.getName()));
                meta.setLore(lore);

                //Update meta
                victimHead.setItemMeta(meta);

                //Give head
                PlayerInventory inv = murderer.getInventory();
                victim.getWorld().dropItemNaturally(victim.getLocation(), victimHead);
            }
            if(cfg.getBoolean("pvp.keepItemsOnMurder"))
            {
                e.setKeepInventory(true);

                //Remove drops
                e.getDrops().clear();

                victim.getTotalExperience();

                int original = victim.getLevel();
                float penalty = (float) cfg.getDouble("pvp.experiencePenalty");

                e.setNewExp( (int) (original * 1-penalty) );
                e.setDroppedExp(original * 10);
            }

            //Update Death Message
            String deathMessage;
            if(cfg.getBoolean("pvp.beheading"))
            {
                deathMessage = strings.get("behead");
            }
            else
            {
                deathMessage = strings.get("murder");
            }
            deathMessage = deathMessage.replace("{x}",victim.getDisplayName());
            deathMessage = deathMessage.replace("{y}",murderer.getDisplayName());
            e.setDeathMessage(deathMessage);
        }
    }
}
