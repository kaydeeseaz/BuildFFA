package rbw.kaydeesea.bffa.listeners;

import com.andrei1058.bedwars.BedWars;
import com.andrei1058.bedwars.listeners.FireballListener;
import com.andrei1058.bedwars.listeners.utils.Utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import rbw.kaydeesea.bffa.objects.LastHit;
import rbw.kaydeesea.bffa.objects.PlayingPlayer;

public class Fireball implements Listener {
   public static HashMap<Player, Long> shootFbs = new HashMap();
   private final double fireballExplosionSize;
   private final double damageSelf;
   private final double damageEnemy;

   public Fireball() {
      this.fireballExplosionSize = BedWars.config.getYml().getDouble("fireball.explosion-size");
      this.damageSelf = BedWars.config.getYml().getDouble("fireball.damage.self");
      this.damageEnemy = BedWars.config.getYml().getDouble("fireball.damage.enemy");
   }

   @EventHandler
   public void fireballPrime(ExplosionPrimeEvent e) {
      if (e.getEntity() instanceof org.bukkit.entity.Fireball && e.getEntity().getWorld().getName().equals(BuildFFA.bffaworld)) {
         e.setFire(false);
         e.setRadius(3.0F);
      }

   }

   @EventHandler
   public void fireballHit(EntityExplodeEvent e) {
      if (e.getEntity() instanceof org.bukkit.entity.Fireball) {
         ProjectileSource projectileSource = ((org.bukkit.entity.Fireball)e.getEntity()).getShooter();
         if (projectileSource instanceof Player) {
            Player source = (Player)projectileSource;
            PlayingPlayer pp = PlayingPlayer.getInstance(source);
            if (pp != null) {
               Location fbLocation = e.getLocation();
               World world = fbLocation.getWorld();
               if (world != null) {
                  Collection<Entity> nearbyEntities = world.getNearbyEntities(fbLocation, this.fireballExplosionSize, this.fireballExplosionSize, this.fireballExplosionSize);

                  if (!e.blockList().isEmpty() && e.getEntity().getWorld().getName().equals(BuildFFA.bffaworld)) {
                     List<Block> destroyed = new ArrayList<>(e.blockList());
                     for (Block block : destroyed) {
                        if (block.getMetadata("placedbyplayer").toString().equals("[]")) {
                           e.blockList().remove(block);
                        }
                     }
                  }

                  for (Entity entity : nearbyEntities) {
                     if (entity instanceof Player) {
                        Player victim = (Player) entity;
                        if (PlayingPlayer.isPlaying(victim)) {
                           double distance = Utils.getDiff(fbLocation, victim.getLocation());
                           double damageReduction = FireballListener.calculateRatio(distance, this.fireballExplosionSize);
                           this.handleDamage(victim, source, damageReduction);
                           double heightForce = 0.95D;
                           double radiusForce = 1.25D;
                           this.pushAway(victim, fbLocation, heightForce, radiusForce);
                        }
                     }
                  }


               }
            }
         }
      }
   }

   void pushAway(Player player, Location l, double hf, double rf) {
      Location loc = player.getLocation();
      double hf1 = Math.max(-4.0D, Math.min(4.0D, hf));
      double rf1 = Math.max(-4.0D, Math.min(4.0D, -1.0D * rf));
      player.setVelocity(l.toVector().subtract(loc.toVector()).normalize().multiply(rf1).setY(hf1));
   }

   public void handleDamage(Player player, Player source, double damageriduction) {
      if (player.equals(source)) {
         if (this.damageSelf > 0.0D) {
            player.damage(this.damageSelf * damageriduction);
         }
      } else {
         LastHit.setLasthit(player, source);
         if (this.damageEnemy > 0.0D) {
            player.damage(this.damageEnemy * damageriduction);
         }
      }

   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void fireballDirectHit(EntityDamageByEntityEvent e) {
      if (e.getDamager() instanceof org.bukkit.entity.Fireball && e.getEntity() instanceof Player) {
         Player player = (Player)e.getEntity();
         if (PlayingPlayer.isPlaying(player)) {
            e.setCancelled(true);
         }
      }

   }

   @EventHandler
   public void onInteract(PlayerInteractEvent e) {
      ItemStack inHand = e.getItem();
      if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
         if (inHand == null) {
            return;
         }

         if (!PlayingPlayer.isPlaying(e.getPlayer())) {
            return;
         }

         long now = System.currentTimeMillis();
         long lasthit = shootFbs.getOrDefault(e.getPlayer(), -1L);
         if (lasthit != -1L && now - lasthit < 1500L) {
            e.getPlayer().sendMessage("§4You are on fireball cooldown.");
            e.setCancelled(true);
            return;
         }

         if (inHand.getType() == BedWars.nms.materialFireball()) {
            e.setCancelled(true);
            shootFbs.put(e.getPlayer(), System.currentTimeMillis());
            Utils.shootFB(e.getPlayer(), inHand, (float)this.fireballExplosionSize, 10.0D);
         }
      }

   }
}
