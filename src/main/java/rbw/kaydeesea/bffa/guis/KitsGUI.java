package rbw.kaydeesea.bffa.guis;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import rbw.kaydeesea.bffa.listeners.BuildFFA;
import rbw.kaydeesea.bffa.objects.Kit;
import rbw.kaydeesea.bffa.objects.PlayingPlayer;

public class KitsGUI implements Listener {
   public static HashMap<Player, HashMap<Integer, Kit>> kits = new HashMap();
   static String title = "§5Settings";

   public static void giveInv(Player p) {
      Inventory temp = Bukkit.createInventory(null, 54, title);
      int[] slots = new int[]{9, 17, 18, 26, 27, 35, 36, 44};

      for (int i = 0; i < 9; ++i) {
         temp.setItem(i, GUIManager.createSeparator("§7", "§7"));
      }

      for (int i = 45; i < 54; ++i) {
         temp.setItem(i, GUIManager.createSeparator("§7", "§7"));
      }

      for (int slot : slots) {
         temp.setItem(slot, GUIManager.createSeparator("§7", "§7"));
      }

      int[] usableSlots = new int[]{20, 21, 22, 23, 24, 29, 30, 31, 32, 33};
      HashMap<Integer, Kit> kks = new HashMap<>();

      for (int i = 0; i < Kit.kits.size(); ++i) {
         Kit kit = Kit.kits.get(i);
         int place = i >= usableSlots.length ? 37 + (i - usableSlots.length) : usableSlots[i];
         String nopermmsg;

         if (kit.permission != null) {
            if (p.hasPermission(kit.permission)) {
               nopermmsg = "§aClick to set this kit!";
            } else {
               nopermmsg = "§cYou don't own this kit!";
            }
         } else {
            nopermmsg = "§aClick to set this kit!";
         }

         temp.setItem(place, GUIManager.createGuiItem(kit.armors.get(1).getType(), "§b" + kit.kitname, "§7", nopermmsg));
         kks.put(place, kit);
      }

      kits.put(p, kks);
      p.openInventory(temp);

   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent e) {
      if (e.getInventory().getTitle().equals(title)) {
         e.setCancelled(true);
         Player p = (Player)e.getWhoClicked();
         ItemStack clickedItem = e.getCurrentItem();
         if (clickedItem != null && !clickedItem.getType().equals(Material.AIR)) {
            if (kits.containsKey(p)) {
               HashMap<Integer, Kit> g = kits.get(p);
               if (g.containsKey(e.getRawSlot())) {
                  Kit kit = g.get(e.getRawSlot());
                  PlayingPlayer pf = PlayingPlayer.getInstance(p);
                  if (pf == null) {
                     return;
                  }

                  if (pf.getSelectedkit().kitname.equals(kit.kitname)) {
                     p.sendMessage("§4You already have this kit");
                     p.closeInventory();
                     return;
                  }

                  p.sendMessage("§aSuccessfully set kit " + kit.kitname + " as your kit.");
                  p.closeInventory();
                  pf.setSelectedkit(kit);
                  BuildFFA.giveKit(p);
               }
            }

         }
      }
   }

   @EventHandler
   public void InventoryBlocker(InventoryDragEvent e) {
      if (e.getInventory().getTitle().equals(title)) {
         e.setCancelled(true);
      }

   }
}
