package uk.lsuth.mc.foundation.enchant;

import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;

public class EnchantRegistry
{
    private HashMap<String,FoundationEnchant> enchants;

    public EnchantRegistry()
    {
        enchants = new HashMap<String,FoundationEnchant>();
    }

    public void register(FoundationEnchant ench)
    {

    }

    public void dispatch(String enchantmentID, int enchantmentLevel, PlayerInteractEvent event)
    {
        FoundationEnchant enchant = enchants.get(enchantmentID);

        enchant.handle(enchantmentLevel,event);

    }

}
