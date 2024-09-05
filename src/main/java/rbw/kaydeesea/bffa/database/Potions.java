package rbw.kaydeesea.bffa.database;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class Potions {
   static File f;
   static FileConfiguration db;

   public static void load() {
      f = new File("plugins/BuildFFA", "potions.yml");
      db = YamlConfiguration.loadConfiguration(f);
   }

   public static ItemStack getSpeed() {
      return (ItemStack)db.get("speed");
   }

   public static ItemStack getJump() {
      return (ItemStack)db.get("jump");
   }

   public static ItemStack getInvisibility() {
      return (ItemStack)db.get("invisibility");
   }
}
