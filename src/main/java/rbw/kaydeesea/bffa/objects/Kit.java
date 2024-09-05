package rbw.kaydeesea.bffa.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import rbw.kaydeesea.bffa.Main;

public class Kit {
   static FileConfiguration config = Main.instance.getConfig();
   public static ArrayList<Kit> kits = new ArrayList<>();
   public ArrayList<ItemStack> armors = new ArrayList<>();
   public Map<Integer, ItemStack> items = new HashMap<>();
   public String permission;
   public String kitname;

   public static void loadKits() {
      for (String kit : config.getStringList("kitslist")) {
         new Kit(kit);
      }

   }

   public Kit(String name) {
      this.kitname = name;
      String[] armorsa = new String[]{"cap", "chestplate", "leggins", "boots"};

       for (String armor : armorsa) {
           ItemStack f = config.getItemStack("kits." + name + ".armor." + armor);
           ItemMeta a = f.getItemMeta();
           a.spigot().setUnbreakable(true);
           f.setItemMeta(a);
           this.armors.add(f);
       }

      for(int i = 0; i < 18; ++i) {
         if (config.isItemStack("kits." + name + ".items.slot" + i)) {
            ItemStack f = config.getItemStack("kits." + name + ".items.slot" + i);
            ItemMeta a = f.getItemMeta();
            a.spigot().setUnbreakable(true);
            f.setItemMeta(a);
            Main.materials.removeIf((mat) -> mat.equals(f.getType()));
            Main.materials.add(f.getType());
            this.items.put(i, f);
         }
      }

      this.permission = config.getString("kitperms." + name).equals("NONE") ? null : config.getString("kitperms." + name);
      kits.add(this);
   }

}
