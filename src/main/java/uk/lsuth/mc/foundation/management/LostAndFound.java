package uk.lsuth.mc.foundation.management;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.data.DataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class LostAndFound extends FoundationCommand implements Listener
{
    DataManager dmgr;
    Logger log;
    FoundationCore core;
    Document doc;

    private static final String docname = "lostnfound";
    private String lftitle;
    private static final int maxsize = 54;

    Inventory inv;
    ArrayList<ItemStack> itemStacks;

    public LostAndFound(FoundationCore core)
    {
        super("found");
        this.core = core;
        dmgr = core.getDmgr();
        log = core.log;

        itemStacks = new ArrayList<ItemStack>();

        lftitle = core.getLmgr().getCommandStrings("lostfound").get("guiTitle");

        if(dmgr.miscDocExists(docname))
        {
            doc = dmgr.fetchMiscDoc(docname);
        }
        else
        {
            doc = new Document();
            doc.put("name",docname);
            doc.put("items",new ArrayList<String>());
            dmgr.registerMiscDoc(docname,doc);
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

    @EventHandler
    public void onItemDespawn(ItemDespawnEvent e)
    {
        ItemStack i = e.getEntity().getItemStack();

        if(!isBlacklisted(i))
        {
            log.info("Saved " + e.getEntity().getItemStack().getI18NDisplayName() + " from despawn");
            itemStacks.add(e.getEntity().getItemStack());
            exportToDoc();
        }

        if(itemStacks.size() > maxsize)
        {
            prune();
        }

    }

    private void prune()
    {
        long t = System.currentTimeMillis();

        for(ItemStack i: itemStacks)
        {
            if(i.getMaxStackSize() > 1 && i.getAmount() != 0)
            {
                for(ItemStack j: itemStacks)
                {
                    if(!(i == j))
                    {
                        if(i.isSimilar(j))
                        {
                            if(i.getAmount() + j.getAmount() <= i.getMaxStackSize())
                            {
                                i.add(j.getAmount());
                                j.setAmount(0);
                            }
                        }
                    }
                }
            }
        }

        for(int i = 0; i < itemStacks.size(); i++)
        {
            if(itemStacks.get(i).getAmount() == 0)
            {
                itemStacks.remove(i);
                i--;
            }
        }

        while(itemStacks.size() > maxsize)
        {
            itemStacks.remove(0);
        }

        exportToDoc();

        t = System.currentTimeMillis() - t;
        log.info("Pruned lost+found list in "+t+"ms.");

    }

    @EventHandler
    public void onItemDamage(EntityDamageEvent e)
    {
        if(e.getEntity() instanceof Item i)
        {
            String cause;
            switch (e.getCause())
            {
                case LAVA:
                    cause = "lava";
                    break;
                case FIRE:
                case FIRE_TICK:
                    cause = "fire";
                    break;
                case ENTITY_EXPLOSION:
                case BLOCK_EXPLOSION:
                    cause = "an explosion";
                    break;
                case VOID:
                    cause = "the void";
                    break;
                case CONTACT:
                    cause = "a cactus";
                    break;
                default:
                    return;
            }

            if(isBlacklisted(i.getItemStack())) return;
            log.info("Saved " + i.getItemStack().getI18NDisplayName() + " from " + cause);
            itemStacks.add(i.getItemStack());
            exportToDoc();
            i.remove();
            if(itemStacks.size() > maxsize)
            {
                prune();
            }
        }
    }


    /** Rendered useless by above code block.
    @EventHandler
    public void onItemBurn(EntityCombustByBlockEvent e)
    {
        if(e.getEntity() instanceof Item)
        {
            Item i = (Item) e.getEntity();
            Material m = i.getItemStack().getType();
            if(!isBlacklisted(m))
            {
                log.info("Saved " + i.getItemStack().getI18NDisplayName() + " from fire");
                itemStacks.add(i.getItemStack());
                exportToDoc();
                i.remove();
            }
        }
    }
    **/


    public static boolean isBlacklisted(ItemStack i)
    {
        //Don't count drops from mobs that burn in the sun.
        //Don't count crap blocks.
        switch (i.getType())
        {
            case ACACIA_SAPLING:
            case ANDESITE:
            case APPLE:
            case ARROW:
            case BEEF:
            case BIRCH_SAPLING:
            case BONE:
            case CHICKEN:
            case COBBLED_DEEPSLATE:
            case COBBLESTONE:
            case COD:
            case DARK_OAK_SAPLING:
            case DIORITE:
            case DIRT:
            case EGG:
            case FEATHER:
            case GHAST_TEAR:
            case GLOW_INK_SAC:
            case GRANITE:
            case GRAVEL:
            case GUNPOWDER:
            case INK_SAC:
            case JUNGLE_SAPLING:
            case LEATHER:
            case MAGMA_CREAM:
            case MUTTON:
            case NETHERRACK:
            case OAK_SAPLING:
            case PORKCHOP:
            case PRISMARINE_CRYSTALS:
            case PRISMARINE_SHARD:
            case RABBIT:
            case RABBIT_HIDE:
            case RED_SAND:
            case ROTTEN_FLESH:
            case SALMON:
            case SAND:
            case SEAGRASS:
            case SLIME_BALL:
            case SNOWBALL:
            case SPIDER_EYE:
            case SPRUCE_SAPLING:
            case STICK:
            case STONE_SWORD:
            case STRING:
            case TROPICAL_FISH:
            case WHEAT_SEEDS:
            case WHITE_WOOL:
            case KELP:
            case BAMBOO:
            case RAIL:
            case LILY_PAD:
                return extendedBlacklist(i);
            default:
                return false;
        }
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    private static boolean extendedBlacklist(ItemStack i)
    {
        if(i.getItemMeta() instanceof Damageable metad)
        {
            //Do not discard anything enchanted
            if(i.getEnchantments().size() != 0)
            {
                return false;
            }
            Material m = i.getType();


            switch(m)
            {
                //FALSE to save
                //TRUE to scrap
                //BREAK to check durability

                case ELYTRA:
                    return false;
                case SHEARS:
                case FLINT_AND_STEEL:
                case FISHING_ROD:
                    break;
                case DIAMOND_HELMET:
                case DIAMOND_CHESTPLATE:
                case DIAMOND_LEGGINGS:
                case DIAMOND_BOOTS:
                case DIAMOND_PICKAXE:
                case DIAMOND_AXE:
                case DIAMOND_SHOVEL:
                case DIAMOND_SWORD:
                case DIAMOND_HOE:
                    return false;
                case GOLDEN_HELMET:
                case GOLDEN_CHESTPLATE:
                case GOLDEN_LEGGINGS:
                case GOLDEN_BOOTS:
                case GOLDEN_PICKAXE:
                case GOLDEN_AXE:
                case GOLDEN_SHOVEL:
                case GOLDEN_SWORD:
                case GOLDEN_HOE:
                    return true;
                case LEATHER:
                case LEATHER_CHESTPLATE:
                case LEATHER_LEGGINGS:
                case LEATHER_BOOTS:
                    break;
                case STONE_PICKAXE:
                case STONE_AXE:
                case STONE_SHOVEL:
                case STONE_SWORD:
                case STONE_HOE:
                    return true;
                case IRON_HELMET:
                case IRON_CHESTPLATE:
                case IRON_LEGGINGS:
                case IRON_BOOTS:
                case IRON_PICKAXE:
                case IRON_AXE:
                case IRON_SHOVEL:
                case IRON_SWORD:
                case IRON_HOE:
                    break;
                case WOODEN_PICKAXE:
                case WOODEN_AXE:
                case WOODEN_SHOVEL:
                case WOODEN_SWORD:
                case WOODEN_HOE:
                    return true;
                default:
                    break;

            }

            //Do not discard anything with 50% or above durability
            float maxd = m.getMaxDurability();
            if((maxd - metad.getDamage()) / maxd >= 0.5f)
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if(!(sender.hasPermission("foundation.lostandfound")))
        {
            sender.sendMessage(FoundationCore.noPermission);
            return true;
        }

        if(args.length == 0 && sender instanceof Player p)
        {
            prune();
            if(inv == null) inv = Bukkit.createInventory(null,54,lftitle);
            else
            {
                inv.clear();
            }
            for(ItemStack is: itemStacks) inv.addItem(is);

            p.openInventory(inv);

            return true;
        }
        return false;
    }

    @EventHandler
    public void inventoryEvent(InventoryClickEvent e)
    {
        String title = e.getWhoClicked().getOpenInventory().getTitle();
        //Prevent spamming console with exceptions every time somebody clicks out of bounds
        if(e.getClickedInventory() == null)
        {
            return;
        }

        if(title.equals(lftitle) && e.getClickedInventory().getType() == InventoryType.CHEST)
        {
            //Only allow extraction.
            if(e.getAction() == InventoryAction.PICKUP_ALL || e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY)
            {
                ItemStack i = e.getCurrentItem();

                int x = -1;
                for(int j = 0; j < itemStacks.size(); j++)
                {
                    if(i.equals(itemStacks.get(j)))
                    {
                        x = j;
                        break;
                    }
                }

                if(x == -1)
                {
                    log.severe("Failed to remove item from lost and found list. Possible item dupe? " + i.getAmount() + " " + i.getI18NDisplayName() + " by " + e.getWhoClicked().getName());
                    e.setCancelled(true);
                }
                else
                {
                    itemStacks.remove(x);

                    Bukkit.getScheduler().runTaskLater(core,() -> {
                        inv.clear();
                        for(ItemStack is: itemStacks) inv.addItem(is);
                    },1);


                    exportToDoc();
                }
            }
            else
            {
                e.setCancelled(true);
            }

        }
    }
}
