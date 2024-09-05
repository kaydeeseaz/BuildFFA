package rbw.kaydeesea.bffa.objects;

import com.andrei1058.bedwars.listeners.utils.Utils;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.kaydeesea.hotbar.objects.HotbarType;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import rbw.kaydeesea.bffa.Main;
import rbw.kaydeesea.bffa.database.Cache;
import rbw.kaydeesea.bffa.listeners.BuildFFA;

public class PlayingPlayer {
   public static ArrayList<PlayingPlayer> instances = new ArrayList<>();
   private final Player p;
   private final Scoreboard scoreboard;
   private int kills;
   private int deaths;
   private int killsstreak;
   private int deathsstreak;
   private Kit selectedkit;
   private boolean build = false;

   public static boolean isPlaying(Player p) {
      boolean e = false;
      for (PlayingPlayer instance : instances) {
         if (instance.getPlayer().equals(p)) {
            e = true;
            break;
         }
      }

      return e;
   }

   public static String getTopKills() {
      String s = "None";
      PlayingPlayer i = null;
      for (PlayingPlayer instance : instances) {
         if (i == null) {
            i = instance;
         } else if (i.getKillStreak() <= instance.getKillStreak()) {
            i = instance;
         }
      }

      return i == null ? s : i.getPlayer().getName();
   }

   public static PlayingPlayer getInstance(Player p) {
      PlayingPlayer Playing = null;
      for (PlayingPlayer instance : instances) {
         if (instance.getPlayer().getName().equals(p.getName())) {
            Playing = instance;
            break;
         }
      }

      return Playing;
   }

   public PlayingPlayer(Player player) {
      BuildFFA.sendMessageInBffa(Main.cfg.getString("messages.join-message").replaceAll("%player%", player.getName()));
      instances.add(this);
      this.p = player;
      this.kills = Cache.getStats(player.getName(), Cache.Stats.KILLS);
      this.deaths = Cache.getStats(player.getName(), Cache.Stats.DEATHS);
      this.selectedkit = Kit.kits.stream().filter((kit) -> kit.permission == null).collect(Collectors.toList()).get(0);
      this.scoreboard = new Scoreboard(player);
      BuildFFA.tpToSpawn(player);
      player.setFlying(false);
      player.setAllowFlight(false);
      player.setGameMode(GameMode.SURVIVAL);
   }

   public Player getPlayer() {
      return this.p;
   }

   public int getDeaths() {
      return this.deaths;
   }

   public int getKills() {
      return this.kills;
   }

   public int getDeathStreak() {
      return this.deathsstreak;
   }

   public int getKillStreak() {
      return this.killsstreak;
   }

   public void increaseDeaths() {
      this.getPlayer().teleport(Main.bffaloc);
      this.getPlayer().setHealth(20.0D);
      ++this.deaths;
      ++this.deathsstreak;
      this.killsstreak = 0;
      for (Entity entity : this.getPlayer().getWorld().getEntities()) {
         if (entity instanceof EnderPearl) {
            EnderPearl pearl = (EnderPearl)entity;
            Player p = (Player)pearl.getShooter();
            if (p.getName().equals(this.getPlayer().getName())) {
               entity.remove();
            }
         }
      }

      for (PotionEffect activePotionEffect : this.getPlayer().getActivePotionEffects()) {
         this.getPlayer().removePotionEffect(activePotionEffect.getType());
      }

      Main.runBukkitTask(() -> {
         BuildFFA.giveKit(this.getPlayer());

         for (PlayingPlayer instance : instances) {
            instance.scoreboard.update();
         }

         this.getPlayer().teleport(Main.bffaloc);
         this.getPlayer().setHealth(20.0D);
      }, 2L);
   }

   public void increaseKills() {
      ++this.kills;
      ++this.killsstreak;
      this.deathsstreak = 0;
      Cache.updateMaxes(this);

      try {
         this.getPlayer().playSound(this.getPlayer().getLocation(), Sound.valueOf("ORB_PICKUP"), 1.0F, 2.0F);
      } catch (Exception var2) {
         System.out.println("Something went wrong! (ORB_PICKUP)");
      }

      ArrayList<ItemStack> s = KillReward.get(this.killsstreak);
      if (s != null) {
         Main.runBukkitTask(() -> {
            for (ItemStack itemStack : s) {
               if (itemStack == null) continue;

               if (itemStack.getType() == Material.POTION) {
                  boolean b = false;

                  for (int i = 0; i < 30; ++i) {
                     ItemStack it = this.getPlayer().getInventory().getItem(i);
                     if (it != null && it.getType() == Material.POTION && it.getData().getData() == itemStack.getData().getData()) {
                        it.setAmount(it.getAmount() + 1);
                        b = true;
                        break;
                     }
                  }

                  if (b) continue;
               }
               HotbarType type = HotbarType.getFromMaterial(itemStack.getType());
               if (type != null) {
                  int slot = type.getPlayerSlot(p);
                  if (slot > -1 && this.getPlayer().getInventory().getItem(slot) == null) {
                     this.getPlayer().getInventory().setItem(slot, itemStack);
                  } else {
                     this.getPlayer().getInventory().addItem(itemStack);
                  }
               } else {
                  this.getPlayer().getInventory().addItem(itemStack);
               }
            }

            for (PlayingPlayer instance : instances) {
               instance.scoreboard.update();
            }

         }, 2L);
      }
   }

   public Kit getSelectedkit() {
      return this.selectedkit;
   }

   public void setSelectedkit(Kit kit) {
      this.selectedkit = kit;
   }

   public boolean isBuild() {
      return this.build;
   }

   public void setBuild(boolean a) {
      this.build = a;
   }

   public void remove(boolean manual) {
      instances.remove(this);
      Cache.setStats(this);
      BuildFFA.sendMessageInBffa(Main.cfg.getString("messages.leave-message").replaceAll("%player%", this.p.getName()));
      for (PlayingPlayer instance : instances) {
         instance.scoreboard.update();
      }

      if (manual) {
         Utils.handleLobbyJoin(this.p);
      }

   }
}
