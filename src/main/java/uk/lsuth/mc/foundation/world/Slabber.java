package uk.lsuth.mc.foundation.world;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import uk.lsuth.mc.foundation.FoundationCommand;
import uk.lsuth.mc.foundation.FoundationCore;
import uk.lsuth.mc.foundation.data.DataManager;
import uk.lsuth.mc.foundation.data.PlayerDataWrapper;

import java.util.HashMap;
import java.util.Map;

public class Slabber extends FoundationCommand implements Listener
{
    FoundationCore core;
    DataManager dmgr;

    Map<String,String> strings;

    private static final Map<Material, Material> TO_SLABS = new HashMap<>();
    private static final Map<Material, Material> TO_BLOCKS = new HashMap<>();

    private static void registerMaterial(Material block, Material slab)
    {
        TO_SLABS.put(block, slab);
        TO_BLOCKS.put(slab, block);
    }

    static
    {
        registerMaterial(Material.OAK_PLANKS, Material.OAK_SLAB);
        registerMaterial(Material.BIRCH_PLANKS, Material.BIRCH_SLAB);
        registerMaterial(Material.SPRUCE_PLANKS, Material.SPRUCE_SLAB);
        registerMaterial(Material.JUNGLE_PLANKS, Material.JUNGLE_SLAB);
        registerMaterial(Material.ACACIA_PLANKS, Material.ACACIA_SLAB);
        registerMaterial(Material.DARK_OAK_PLANKS, Material.DARK_OAK_SLAB);
        registerMaterial(Material.SANDSTONE, Material.SANDSTONE_SLAB);
        registerMaterial(Material.SMOOTH_SANDSTONE, Material.SMOOTH_SANDSTONE_SLAB);
        registerMaterial(Material.CUT_SANDSTONE, Material.CUT_SANDSTONE_SLAB);
        registerMaterial(Material.RED_SANDSTONE, Material.RED_SANDSTONE_SLAB);
        registerMaterial(Material.SMOOTH_RED_SANDSTONE, Material.SMOOTH_RED_SANDSTONE_SLAB);
        registerMaterial(Material.CUT_RED_SANDSTONE, Material.CUT_RED_SANDSTONE_SLAB);
        registerMaterial(Material.COBBLESTONE, Material.COBBLESTONE_SLAB);
        registerMaterial(Material.STONE, Material.STONE_SLAB);
        registerMaterial(Material.BRICK, Material.BRICK_SLAB);
        registerMaterial(Material.STONE_BRICKS, Material.STONE_BRICK_SLAB);
        registerMaterial(Material.MOSSY_STONE_BRICKS, Material.MOSSY_STONE_BRICK_SLAB);
        registerMaterial(Material.QUARTZ_BLOCK, Material.QUARTZ_SLAB);
        registerMaterial(Material.SMOOTH_QUARTZ, Material.SMOOTH_QUARTZ_SLAB);
        registerMaterial(Material.NETHER_BRICK, Material.NETHER_BRICK_SLAB);
        registerMaterial(Material.RED_NETHER_BRICKS, Material.RED_NETHER_BRICK_SLAB);
        registerMaterial(Material.PURPUR_BLOCK, Material.PURPUR_SLAB);
        registerMaterial(Material.SMOOTH_STONE, Material.SMOOTH_STONE_SLAB);
        registerMaterial(Material.GRANITE, Material.GRANITE_SLAB);
        registerMaterial(Material.POLISHED_GRANITE, Material.POLISHED_GRANITE_SLAB);
        registerMaterial(Material.ANDESITE, Material.ANDESITE_SLAB);
        registerMaterial(Material.POLISHED_ANDESITE, Material.POLISHED_ANDESITE_SLAB);
        registerMaterial(Material.DIORITE, Material.DIORITE_SLAB);
        registerMaterial(Material.POLISHED_DIORITE, Material.POLISHED_DIORITE_SLAB);
        registerMaterial(Material.END_STONE_BRICKS, Material.END_STONE_BRICK_SLAB);
        registerMaterial(Material.MOSSY_COBBLESTONE, Material.MOSSY_COBBLESTONE_SLAB);
        registerMaterial(Material.PRISMARINE, Material.PRISMARINE_SLAB);
        registerMaterial(Material.DARK_PRISMARINE, Material.DARK_PRISMARINE_SLAB);
        registerMaterial(Material.PRISMARINE_BRICKS, Material.PRISMARINE_BRICK_SLAB);
    }

    public Slabber(FoundationCore core)
    {
        super("slab");
        this.core = core;
        dmgr = core.getDmgr();
        strings = core.getLmgr().getCommandStrings("slab");
        createRecipes();
    }

    private void createRecipes()
    {
        for(Material slab:TO_BLOCKS.keySet())
        {
            NamespacedKey ns = new NamespacedKey(core,"slab-combine-" + slab.toString());
            ShapelessRecipe r = new ShapelessRecipe(ns,new ItemStack(toBlock(slab),1));
            r.addIngredient(2,slab);
            Bukkit.addRecipe(r);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e)
    {
        Player p = e.getPlayer();

        if (!p.isSneaking())
        {
            return;
        }

        Block b = e.getBlock();
        BlockData bd = b.getBlockData();

        if (bd == null || !(bd instanceof Slab))
        {
            Material x = toSlab(b.getType());
            if (x == null)
            {
                return;
            }
            else
            {
                if(!isSlabberEnabled(p)) return;

                b.setType(x);
                Slab y = (Slab) b.getBlockData();
                y.setType(Slab.Type.DOUBLE);
                b.setBlockData(y);
            }
        }

        bd = b.getBlockData();
        Slab slab = (Slab) bd;

        if (slab.getType() == Slab.Type.DOUBLE)
        {
            BlockFace bf = p.getTargetBlockFace(8);

            if (bf == BlockFace.UP)
            {
                slab.setType(Slab.Type.BOTTOM);
                drop(b);
                e.setCancelled(true);
                b.setBlockData(bd);
            }
            else if (bf == BlockFace.DOWN)
            {
                slab.setType(Slab.Type.TOP);
                drop(b);
                e.setCancelled(true);
                b.setBlockData(bd);
            }
            else
            {
                RayTraceResult rtr = p.rayTraceBlocks(8);

                if (rtr == null)
                {
                    return;
                }

                Vector v = rtr.getHitPosition();
                if (v.getY() % 1 >= 0.6)
                {
                    slab.setType(Slab.Type.BOTTOM);
                    drop(b);
                    e.setCancelled(true);
                    b.setBlockData(bd);
                }
                else if (v.getY() % 1 <= 0.4)
                {
                    slab.setType(Slab.Type.TOP);
                    drop(b);
                    e.setCancelled(true);
                    b.setBlockData(bd);
                }
                else
                {
                    //Normal Behaviour
                    return;
                }
            }
        }
    }

    private void drop(Block b)
    {
        ItemStack is = new ItemStack(b.getType(), 1);
        b.getWorld().dropItemNaturally(b.getLocation(), is);
    }

    private Material toSlab(Material m)
    {
        return TO_SLABS.get(m);
    }

    private Material toBlock(Material m)
    {
        return TO_BLOCKS.get(m);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(sender instanceof Player)
        {
            toggleSlabber((Player) sender);
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean isSlabberEnabled(Player p)
    {
        PlayerDataWrapper pdw = dmgr.fetchData(p);
        Document pdoc = pdw.getPlayerDocument();

        if(pdoc.containsKey("slabber"))
        {
            if(pdoc.getBoolean("slabber"))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            pdoc.put("slabber",false);
            return false;
        }
    }

    private void toggleSlabber(Player p)
    {
        PlayerDataWrapper pdw = dmgr.fetchData(p);
        Document pdoc = pdw.getPlayerDocument();

        if(pdoc.containsKey("slabber"))
        {
            if(pdoc.getBoolean("slabber"))
            {
                pdoc.put("slabber",false);
                p.sendMessage(strings.get("disabled"));
            }
            else
            {
                pdoc.put("slabber",true);
                p.sendMessage(strings.get("enabled"));
            }
        }
        else
        {
            pdoc.put("slabber",true);
            p.sendMessage(strings.get("enabled"));
        }
    }

}
