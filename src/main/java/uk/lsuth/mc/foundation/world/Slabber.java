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

import java.util.Map;

public class Slabber extends FoundationCommand implements Listener
{
    FoundationCore core;
    DataManager dmgr;

    Map<String,String> strings;

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
        for(Material slab:slabs)
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
        switch (m)
        {
            case OAK_PLANKS:
                return Material.OAK_SLAB;
            case BIRCH_PLANKS:
                return Material.BIRCH_SLAB;
            case SPRUCE_PLANKS:
                return Material.SPRUCE_SLAB;
            case JUNGLE_PLANKS:
                return Material.JUNGLE_SLAB;
            case ACACIA_PLANKS:
                return Material.ACACIA_SLAB;
            case DARK_OAK_PLANKS:
                return Material.DARK_OAK_SLAB;
            case SANDSTONE:
                return Material.SANDSTONE_SLAB;
            case SMOOTH_SANDSTONE:
                return Material.SMOOTH_SANDSTONE_SLAB;
            case CUT_SANDSTONE:
                return Material.CUT_SANDSTONE_SLAB;
            case RED_SANDSTONE:
                return Material.RED_SANDSTONE_SLAB;
            case SMOOTH_RED_SANDSTONE:
                return Material.SMOOTH_RED_SANDSTONE_SLAB;
            case CUT_RED_SANDSTONE:
                return Material.CUT_RED_SANDSTONE_SLAB;
            case COBBLESTONE:
                return Material.COBBLESTONE_SLAB;
            case STONE:
                return Material.STONE_SLAB;
            case BRICK:
                return Material.BRICK_SLAB;
            case STONE_BRICKS:
                return Material.STONE_BRICK_SLAB;
            case MOSSY_STONE_BRICKS:
                return Material.MOSSY_STONE_BRICK_SLAB;
            case QUARTZ_BLOCK:
                return Material.QUARTZ_SLAB;
            case SMOOTH_QUARTZ:
                return Material.SMOOTH_QUARTZ_SLAB;
            case NETHER_BRICK:
                return Material.NETHER_BRICK_SLAB;
            case RED_NETHER_BRICKS:
                return Material.RED_NETHER_BRICK_SLAB;
            case PURPUR_BLOCK:
                return Material.PURPUR_SLAB;
            case SMOOTH_STONE:
                return Material.SMOOTH_STONE_SLAB;
            case GRANITE:
                return Material.GRANITE_SLAB;
            case POLISHED_GRANITE:
                return Material.POLISHED_GRANITE_SLAB;
            case ANDESITE:
                return Material.ANDESITE_SLAB;
            case POLISHED_ANDESITE:
                return Material.POLISHED_ANDESITE_SLAB;
            case DIORITE:
                return Material.DIORITE_SLAB;
            case POLISHED_DIORITE:
                return Material.POLISHED_DIORITE_SLAB;
            case END_STONE_BRICKS:
                return Material.END_STONE_BRICK_SLAB;
            case MOSSY_COBBLESTONE:
                return Material.MOSSY_COBBLESTONE_SLAB;
            case PRISMARINE:
                return Material.PRISMARINE_SLAB;
            case DARK_PRISMARINE:
                return Material.DARK_PRISMARINE_SLAB;
            case PRISMARINE_BRICKS:
                return Material.PRISMARINE_BRICK_SLAB;
            default:
                return null;
        }
    }

    private Material toBlock(Material m)
    {
        switch (m)
        {
            case OAK_SLAB:
                return Material.OAK_PLANKS;
            case BIRCH_SLAB:
                return Material.BIRCH_PLANKS;
            case SPRUCE_SLAB:
                return Material.SPRUCE_PLANKS;
            case JUNGLE_SLAB:
                return Material.JUNGLE_PLANKS;
            case ACACIA_SLAB:
                return Material.ACACIA_PLANKS;
            case DARK_OAK_SLAB:
                return Material.DARK_OAK_PLANKS;
            case SANDSTONE_SLAB:
                return Material.SANDSTONE;
            case SMOOTH_SANDSTONE_SLAB:
                return Material.SMOOTH_SANDSTONE;
            case CUT_SANDSTONE_SLAB:
                return Material.CUT_SANDSTONE;
            case RED_SANDSTONE_SLAB:
                return Material.RED_SANDSTONE;
            case SMOOTH_RED_SANDSTONE_SLAB:
                return Material.SMOOTH_RED_SANDSTONE;
            case CUT_RED_SANDSTONE_SLAB:
                return Material.CUT_RED_SANDSTONE;
            case COBBLESTONE_SLAB:
                return Material.COBBLESTONE;
            case STONE_SLAB:
                return Material.STONE;
            case BRICK_SLAB:
                return Material.BRICK;
            case STONE_BRICK_SLAB:
                return Material.STONE_BRICKS;
            case MOSSY_STONE_BRICK_SLAB:
                return Material.MOSSY_STONE_BRICKS;
            case QUARTZ_SLAB:
                return Material.QUARTZ_BLOCK;
            case SMOOTH_QUARTZ_SLAB:
                return Material.SMOOTH_QUARTZ;
            case NETHER_BRICK_SLAB:
                return Material.NETHER_BRICK;
            case RED_NETHER_BRICK_SLAB:
                return Material.RED_NETHER_BRICKS;
            case PURPUR_SLAB:
                return Material.PURPUR_BLOCK;
            case SMOOTH_STONE_SLAB:
                return Material.SMOOTH_STONE;
            case GRANITE_SLAB:
                return Material.GRANITE;
            case POLISHED_GRANITE_SLAB:
                return Material.POLISHED_GRANITE;
            case ANDESITE_SLAB:
                return Material.ANDESITE;
            case POLISHED_ANDESITE_SLAB:
                return Material.POLISHED_ANDESITE;
            case DIORITE_SLAB:
                return Material.DIORITE;
            case POLISHED_DIORITE_SLAB:
                return Material.POLISHED_DIORITE;
            case END_STONE_BRICK_SLAB:
                return Material.END_STONE_BRICKS;
            case MOSSY_COBBLESTONE_SLAB:
                return Material.MOSSY_COBBLESTONE;
            case PRISMARINE_SLAB:
                return Material.PRISMARINE;
            case DARK_PRISMARINE_SLAB:
                return Material.DARK_PRISMARINE;
            case PRISMARINE_BRICK_SLAB:
                return Material.PRISMARINE_BRICKS;
            default:
                return null;
        }
    }

    private final static Material[] slabs =
            {
                Material.OAK_SLAB,

                Material.BIRCH_SLAB,

                Material.SPRUCE_SLAB,

                Material.JUNGLE_SLAB,

                Material.ACACIA_SLAB,

                Material.DARK_OAK_SLAB,

                Material.SANDSTONE_SLAB,

                Material.SMOOTH_SANDSTONE_SLAB,

                Material.CUT_SANDSTONE_SLAB,

                Material.RED_SANDSTONE_SLAB,

                Material.SMOOTH_RED_SANDSTONE_SLAB,

                Material.CUT_RED_SANDSTONE_SLAB,

                Material.COBBLESTONE_SLAB,

                Material.STONE_SLAB,

                Material.BRICK_SLAB,

                Material.STONE_BRICK_SLAB,

                Material.MOSSY_STONE_BRICK_SLAB,

                Material.QUARTZ_SLAB,

                Material.SMOOTH_QUARTZ_SLAB,

                Material.NETHER_BRICK_SLAB,

                Material.RED_NETHER_BRICK_SLAB,

                Material.PURPUR_SLAB,

                Material.SMOOTH_STONE_SLAB,

                Material.GRANITE_SLAB,

                Material.POLISHED_GRANITE_SLAB,

                Material.ANDESITE_SLAB,

                Material.POLISHED_ANDESITE_SLAB,

                Material.DIORITE_SLAB,

                Material.POLISHED_DIORITE_SLAB,

                Material.END_STONE_BRICK_SLAB,

                Material.MOSSY_COBBLESTONE_SLAB,

                Material.PRISMARINE_SLAB,

                Material.DARK_PRISMARINE_SLAB,

                Material.PRISMARINE_BRICK_SLAB
            };

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
