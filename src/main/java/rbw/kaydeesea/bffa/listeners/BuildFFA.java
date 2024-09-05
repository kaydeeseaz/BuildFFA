package rbw.kaydeesea.bffa.listeners;

import java.util.Objects;

import com.kaydeesea.hotbar.objects.HotbarType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import rbw.kaydeesea.bffa.Main;
import rbw.kaydeesea.bffa.objects.LastHit;
import rbw.kaydeesea.bffa.objects.PlayingPlayer;

public class BuildFFA implements Listener {
   public static String bffaworld = "buildffa";

   @EventHandler
   public void onWorldChange(PlayerChangedWorldEvent e) {
      String fromworld = e.getFrom().getName();
      String toworld = e.getPlayer().getWorld().getName();
      if (!toworld.equals(fromworld)) {
         PlayingPlayer pp;
         if (fromworld.equals(bffaworld)) {
            pp = PlayingPlayer.getInstance(e.getPlayer());
            if (pp != null) {
               pp.remove(false);
            }
         }

         if (toworld.equals(bffaworld)) {
            pp = PlayingPlayer.getInstance(e.getPlayer());
            if (pp == null) {
               e.getPlayer().performCommand("bw leave");
            }
         }

      }
   }

   @EventHandler
   public void onLeave(PlayerQuitEvent e) {
      PlayingPlayer pe = PlayingPlayer.getInstance(e.getPlayer());
      if (pe != null) {
         Player attacker = LastHit.getLasthit(e.getPlayer());
         String message;
         if (attacker == null) {
            if (e.getPlayer().getLastDamageCause() != null) {
               DamageCause deathCause = e.getPlayer().getLastDamageCause().getCause();
               if (deathCause == DamageCause.VOID) {
                  message = Main.cfg.getString("messages.death-by-void").replaceAll("%victim%", e.getPlayer().getName());
               } else {
                  message = Main.cfg.getString("messages.death-unknown").replaceAll("%victim%", e.getPlayer().getName());
               }

               KillsAndDeaths.count(e.getPlayer(), message);
            }
         } else {
            int a = Main.cfg.getInt("deathmessages");
            message = Main.cfg.getString("messages.death-by-player-" + Main.randomInt(1, a)).replaceAll("%victim%", e.getPlayer().getName()).replaceAll("%attacker%", attacker.getName());
            KillsAndDeaths.count(e.getPlayer(), attacker, message);
            attacker.setHealth(20.0D);
         }

         PlayingPlayer pp = PlayingPlayer.getInstance(e.getPlayer());
         pp.remove(false);
      }

   }

   @EventHandler
   public void onWorldLoad(WorldLoadEvent e) {
      World w = e.getWorld();
      if (Objects.equals(w.getName(), bffaworld)) {
         Main.bffaloc = (Location)Main.cfg.get("spawnloc");
         System.out.println("§2Buildffa world has been loaded!");
      }

   }

   public static void sendMessageInBffa(String message) {
      System.out.println(message);
      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
         if (onlinePlayer != null && onlinePlayer.getWorld().getName().equals(bffaworld)) {
            onlinePlayer.sendMessage(message);
         }
      }

   }

   public static void giveKit(Player player) {
      PlayingPlayer p = PlayingPlayer.getInstance(player);
      if (p == null) {
         player.sendMessage("§4An error has occured. Please contact a developer sending him this message");
      } else {
         player.getInventory().clear();
         player.getInventory().setHelmet(p.getSelectedkit().armors.get(0));
         player.getInventory().setChestplate(p.getSelectedkit().armors.get(1));
         player.getInventory().setLeggings(p.getSelectedkit().armors.get(2));
         player.getInventory().setBoots(p.getSelectedkit().armors.get(3));

         for(int i = 0; i < 18; ++i) {
            if (p.getSelectedkit().items.containsKey(i)) {
               Material item = p.getSelectedkit().items.get(i).getType();
               HotbarType type = HotbarType.getFromMaterial(item);
               if (type != null) {
                  int slot = type.getPlayerSlot(player);
                  if (slot > -1 && player.getInventory().getItem(slot) == null) {
                     player.getInventory().setItem(slot, p.getSelectedkit().items.get(i));
                  } else {
                     player.getInventory().addItem(p.getSelectedkit().items.get(i));
                  }
               } else {
                  player.getInventory().setItem(i, p.getSelectedkit().items.get(i));
               }
            }
         }

      }
   }

   public static void tpToSpawn(Player player) {
      player.teleport(Main.bffaloc);
      giveKit(player);
   }

   public static boolean isPlayerInSpawn(Player player) {
      return player.getLocation().getY() > (double)(Main.maxy + 15);
   }
}
