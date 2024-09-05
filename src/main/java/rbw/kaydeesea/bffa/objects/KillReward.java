package rbw.kaydeesea.bffa.objects;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.inventory.ItemStack;
import rbw.kaydeesea.bffa.Main;
import rbw.kaydeesea.bffa.database.Potions;

public class KillReward {
   public static HashMap<Integer, ArrayList<ItemStack>> rewards = new HashMap<>();
   public static ArrayList<ItemStack> others;

   public static void load() {
      for(int i = 1; i < Integer.MAX_VALUE; ++i) {
         String s = "killsrewards." + i;
         if (!Main.cfg.isSet("killsrewards." + i)) {
            break;
         }

         ArrayList<ItemStack> result = new ArrayList<>();

         for(int j = 1; j < 9; ++j) {
            String item = s + ".item" + j;
            if (!Main.cfg.isSet(item)) {
               break;
            }

            if (Main.cfg.isSet(item + ".potion")) {
               String potion = Main.cfg.getString(item + ".potion");
               if (potion.equals("jump")) {
                  result.add(Potions.getJump());
               }

               if (potion.equals("speed")) {
                  result.add(Potions.getSpeed());
               }

               if (potion.equals("invisibility")) {
                  result.add(Potions.getInvisibility());
               }
            } else {
               try {
                  ItemStack stack = (ItemStack)Main.cfg.get(item);
                  if (stack != null) {
                     result.add(stack);
                  }
               } catch (Exception var7) {
                  System.out.println("[BuildFFA] Couldn't load" + item);
               }
            }
         }

         rewards.put(i, result);
      }

      ArrayList<ItemStack> result = new ArrayList<>();
      String s = "killsrewards.others";
      if (Main.cfg.isSet(s)) {
         for(int i = 1; i < 9; ++i) {
            String item = s + ".item" + i;
            if (!Main.cfg.isSet(item)) {
               break;
            }

            if (Main.cfg.isSet(item + ".potion")) {
               String potion = Main.cfg.getString(item + ".potion");
               if (potion.equals("jump")) {
                  result.add(Potions.getJump());
               }

               if (potion.equals("speed")) {
                  result.add(Potions.getSpeed());
               }

               if (potion.equals("invisibility")) {
                  result.add(Potions.getInvisibility());
               }
            } else {
               try {
                  ItemStack stack = (ItemStack)Main.cfg.get(item);
                  if (stack != null) {
                     result.add(stack);
                  }
               } catch (Exception var6) {
                  System.out.println("[BuildFFA] Couldn't load" + item);
               }
            }
         }
      }

      others = result;
   }

   public static ArrayList<ItemStack> get(int killcount) {
      return rewards.getOrDefault(killcount, others);
   }
}
