package rbw.kaydeesea.bffa.guis;

import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUIManager {
   public static ItemStack createGuiItem(Material material, String name, String... lore) {
      ItemStack item = new ItemStack(material, 1);
      ItemMeta meta = item.getItemMeta();
      meta.setDisplayName(name);
      meta.setLore(Arrays.asList(lore));
      item.setItemMeta(meta);
      return item;
   }

   public static ItemStack createSeparator(String name, String... lore) {
      ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)8);
      ItemMeta meta = item.getItemMeta();
      meta.setDisplayName(name);
      meta.setLore(Arrays.asList(lore));
      item.setItemMeta(meta);
      return item;
   }
}
