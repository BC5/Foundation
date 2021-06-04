package uk.lsuth.mc.foundation.management;

import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.inventory.ItemStack;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.data.DataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class LostAndFound implements Listener
{
    DataManager dmgr;
    Logger log;

    Document doc;

    private static final String docname = "lostnfound";

    ArrayList<ItemStack> itemStacks;

    public LostAndFound(FoundationCore core)
    {
        dmgr = core.getDmgr();
        log = core.log;

        itemStacks = new ArrayList<ItemStack>();

        if(dmgr.miscDocExists(docname))
        {
            doc = dmgr.fetchMiscDoc(docname);
        }
        else
        {
            doc = new Document();
            doc.put("name",docname);
            dmgr.saveMiscDoc(docname,doc);
        }

        importFromDoc();
    }

    private void exportToDoc()
    {
        long t = System.currentTimeMillis();
        ArrayList<String> itemStrings = new ArrayList<String>();
        for(ItemStack is: itemStacks)
        {
            YamlConfiguration yml = new YamlConfiguration();
            yml.set("item",is);
            itemStrings.add(yml.saveToString());
        }
        doc.put("items",itemStrings);
        t = System.currentTimeMillis() - t;

        if(t > 100)
        {
            log.warning("Lost+Found ItemStack exporting took "+t+"ms.");
        }

    }

    private void importFromDoc()
    {
        List<String> itemStrings = doc.getList("items",String.class);
        itemStacks.clear();
        for(String s: itemStrings)
        {
            try
            {
                YamlConfiguration itemcfg = new YamlConfiguration();
                itemcfg.loadFromString(s);
                ItemStack is = itemcfg.getItemStack("item");
                itemStacks.add(is);
            }
            catch (InvalidConfigurationException e)
            {
                log.severe("Corrupted YAML ItemStack. Ignoring");
                log.severe(s);
            }
        }
    }

    public void onItemDespawn(ItemDespawnEvent e)
    {
        Material m = e.getEntity().getItemStack().getType();

        if(!isBlacklisted(m))
        {
            itemStacks.add(e.getEntity().getItemStack());
            exportToDoc();
        }

    }

    public void onItemBurn(EntityCombustByBlockEvent e)
    {
        if(e.getEntity() instanceof Item)
        {
            Item i = (Item) e.getEntity();
            Material m = i.getItemStack().getType();
            if(!isBlacklisted(m))
            {
                itemStacks.add(i.getItemStack());
                exportToDoc();
                i.remove();
            }
        }
    }


    public static boolean isBlacklisted(Material M)
    {
        //Don't count drops from mobs that burn in the sun.
        //Don't count crap blocks.
        switch (M)
        {
            case ROTTEN_FLESH:
            case BONE:
            case ARROW:
            case STONE_SWORD:
            case COBBLESTONE:
            case ANDESITE:
            case GRANITE:
            case DIORITE:
            case NETHERRACK:
            case DIRT:
            case WHITE_WOOL:
            case MUTTON:
                return true;
            default:
                return false;
        }
    }

}
