package uk.lsuth.mc.foundation.enchant;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;


public class EnchantListener implements Listener
{
    NamespacedKey enchantKey;
    EnchantRegistry registry;

    public EnchantListener(NamespacedKey enchantKey, EnchantRegistry registry)
    {
        this.enchantKey = enchantKey;
        this.registry = registry;
    }


    @EventHandler
    public void playerUse(PlayerInteractEvent e)
    {
        ItemStack tool = e.getItem();

        if(tool == null)
        {
            return;
        }

        if(tool.hasItemMeta())
        {
            ItemMeta meta = tool.getItemMeta();
            String enchantData = meta.getPersistentDataContainer().get(this.enchantKey, PersistentDataType.STRING);
            JsonArray enchantJSON = new JsonParser().parse(enchantData).getAsJsonArray();

            for(JsonElement element:enchantJSON)
            {
                JsonObject obj = element.getAsJsonObject();
                String enchantName = obj.get("name").getAsString();
                int enchantLevel = obj.get("lvl").getAsInt();
                registry.dispatch(enchantName,enchantLevel,e);
            }
        }

    }
}
