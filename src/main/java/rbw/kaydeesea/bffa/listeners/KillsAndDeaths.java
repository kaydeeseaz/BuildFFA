package rbw.kaydeesea.bffa.listeners;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import rbw.kaydeesea.bffa.Main;
import rbw.kaydeesea.bffa.objects.LastHit;
import rbw.kaydeesea.bffa.objects.PlayingPlayer;

public class KillsAndDeaths implements Listener {
   public static FileConfiguration cfg;

   public static void count(Player victim, String message) {
      PlayingPlayer victimp = PlayingPlayer.getInstance(victim);
      victimp.increaseDeaths();
      BuildFFA.sendMessageInBffa(message);
   }

   public static void count(Player victim, Player attacker, String message) {
      PlayingPlayer victimp = PlayingPlayer.getInstance(victim);
      if (victimp != null) {
         victimp.increaseDeaths();
      }

      PlayingPlayer attackerp = PlayingPlayer.getInstance(attacker);
      if (attackerp != null) {
         attackerp.increaseKills();
      }

      BuildFFA.sendMessageInBffa(message);
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onHit(EntityDamageByEntityEvent e) {
      if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
         Player attacker = (Player)e.getDamager();
         Player victim = (Player)e.getEntity();
         if (!PlayingPlayer.isPlaying(victim)) {
            return;
         }

         if (attacker.equals(victim)) {
            e.setCancelled(true);
            return;
         }

         if (BuildFFA.isPlayerInSpawn(attacker)) {
            e.setCancelled(true);
            return;
         }

         LastHit.setLasthit(victim, attacker);
      }

   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onDeath(PlayerDeathEvent e) {
      if (e.getEntity() != null && PlayingPlayer.isPlaying(e.getEntity())) {
         e.setDroppedExp(0);
         e.getDrops().clear();
         e.setDeathMessage("");
         Player victim = e.getEntity();
         Player attacker = LastHit.getLasthit(victim);
         String message;
         if (attacker == null) {
            DamageCause deathCause = victim.getLastDamageCause().getCause();
            if (deathCause.equals(DamageCause.VOID)) {
               message = cfg.getString("messages.death-by-void").replaceAll("%victim%", victim.getName());
            } else {
               message = cfg.getString("messages.death-unknown").replaceAll("%victim%", victim.getName());
            }

            count(victim, message);
         } else {
            int a = cfg.getInt("deathmessages");
            message = cfg.getString("messages.death-by-player-" + Main.randomInt(1, a)).replaceAll("%victim%", victim.getName()).replaceAll("%attacker%", attacker.getName());
            count(victim, attacker, message);
            attacker.setHealth(20.0D);
         }
      }

   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onFallDamage(EntityDamageEvent e) {
      if (e.getEntity() instanceof Player && PlayingPlayer.isPlaying((Player)e.getEntity())) {
         if (e.getCause().equals(DamageCause.FALL)) {
            e.setCancelled(true);
            return;
         }

         Player victim;
         int a;
         if (e.getCause().equals(DamageCause.VOID)) {
            e.setCancelled(true);
            victim = LastHit.getLasthit((Player)e.getEntity());
            String message;
            if (victim == null) {
               message = cfg.getString("messages.death-by-void").replaceAll("%victim%", e.getEntity().getName());
               count((Player)e.getEntity(), message);
            } else {
               a = cfg.getInt("deathmessages");
               message = cfg.getString("messages.death-by-player-" + Main.randomInt(1, a)).replaceAll("%victim%", e.getEntity().getName()).replaceAll("%attacker%", victim.getName());
               count((Player)e.getEntity(), victim, message);
               victim.setHealth(20.0D);
            }

            return;
         }

         if (((Player)e.getEntity()).getHealth() <= e.getFinalDamage()) {
            e.setCancelled(true);
            victim = (Player)e.getEntity();
            BuildFFA.tpToSpawn(victim);
            Player attacker = LastHit.getLasthit(victim);
            String message;
            if (attacker == null) {
               if (victim.getLastDamageCause() == null) {
                  return;
               }

               DamageCause deathCause = victim.getLastDamageCause().getCause();
               if (deathCause.equals(DamageCause.VOID)) {
                  message = cfg.getString("messages.death-by-void").replaceAll("%victim%", victim.getName());
               } else {
                  message = cfg.getString("messages.death-unknown").replaceAll("%victim%", victim.getName());
               }

               count(victim, message);
            } else {
               a = cfg.getInt("deathmessages");
               message = cfg.getString("messages.death-by-player-" + Main.randomInt(1, a)).replaceAll("%victim%", victim.getName()).replaceAll("%attacker%", attacker.getName());
               count(victim, attacker, message);
               attacker.setHealth(20.0D);
            }
         }
      }

   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.HIGH
   )
   public void MoveListener(PlayerMoveEvent e) {
      if (PlayingPlayer.isPlaying(e.getPlayer()) && e.getPlayer().getLocation().getBlockY() < Main.voidheight) {
         Player attacker = LastHit.getLasthit(e.getPlayer());
         String message;
         if (attacker == null) {
            message = cfg.getString("messages.death-by-void").replaceAll("%victim%", e.getPlayer().getName());
            count(e.getPlayer(), message);
         } else {
            int a = cfg.getInt("deathmessages");
            message = cfg.getString("messages.death-by-player-" + Main.randomInt(1, a)).replaceAll("%victim%", e.getPlayer().getName()).replaceAll("%attacker%", attacker.getName());
            count(e.getPlayer(), attacker, message);
            attacker.setHealth(20.0D);
         }

         e.setCancelled(true);
      }

   }

   static {
      cfg = Main.instance.getConfig();
   }
}
