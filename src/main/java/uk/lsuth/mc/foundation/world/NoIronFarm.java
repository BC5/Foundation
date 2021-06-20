package uk.lsuth.mc.foundation.world;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NoIronFarm implements Listener
{

    Random random;

    public NoIronFarm()
    {
        random = new Random();
    }

    @EventHandler
    public void onIronGolemDeath(EntityDeathEvent e)
    {
        if(e.getEntityType() == EntityType.IRON_GOLEM)
        {
            if(e.getDroppedExp() == 0)
            {
                List<ItemStack> drops = e.getDrops();

                for(int i = 0; i < drops.size(); i++)
                {
                    if(drops.get(i).getType() == Material.IRON_INGOT)
                    {
                        drops.remove(i);
                        i--;
                    }
                }

                Material crap;
                String crapname;
                String lore2;

                switch(random.nextInt(5))
                {
                    case 0:
                        crap = Material.PAPER;
                        crapname = "note";
                        lore2 = "we promise to pay the bearer on demand 1 insult";
                        break;
                    case 1:
                        crap = Material.DEAD_BUSH;
                        crapname = "tree";
                        lore2 = "it's a magic iron tree. plant it!";
                        break;
                    case 2:
                        crap = Material.ROTTEN_FLESH;
                        crapname = "jerky";
                        lore2 = "has a metallic taste to it";
                        break;
                    case 3:
                        crap = Material.COARSE_DIRT;
                        crapname = "dirt";
                        lore2 = "plant your magic iron trees on it";
                        break;
                    case 4:
                        crap = Material.JUNGLE_TRAPDOOR;
                        crapname = "door";
                        lore2 = "it's so ugly.";
                        break;
                    default:
                        crap = Material.ACACIA_BOAT;
                        crapname = "boat";
                        lore2 = "this bit of code should never run!?";
                }

                ItemStack note = new ItemStack(crap);
                ItemMeta meta = note.getItemMeta();
                meta.displayName(Component.text("one chump " + crapname,NamedTextColor.GOLD));
                ArrayList<Component> lore = new ArrayList<>();
                lore.add(Component.text(lore2, NamedTextColor.YELLOW));
                lore.add(Component.text("for chumps only!",NamedTextColor.RED));
                meta.lore(lore);
                note.setItemMeta(meta);

                drops.add(note);


            }
        }
    }
}
