package rbw.kaydeesea.bffa;

import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.arena.Arena;
import java.util.ArrayList;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import rbw.kaydeesea.bffa.commands.BuildFFACommand;
import rbw.kaydeesea.bffa.commands.CommandsListener;
import rbw.kaydeesea.bffa.database.Cache;
import rbw.kaydeesea.bffa.database.Potions;
import rbw.kaydeesea.bffa.guis.KitsGUI;
import rbw.kaydeesea.bffa.listeners.Blocking;
import rbw.kaydeesea.bffa.listeners.BuildFFA;
import rbw.kaydeesea.bffa.listeners.Fireball;
import rbw.kaydeesea.bffa.listeners.KillsAndDeaths;
import rbw.kaydeesea.bffa.objects.KillReward;
import rbw.kaydeesea.bffa.objects.Kit;
import rbw.kaydeesea.bffa.objects.PAPIExpansion;
import rbw.kaydeesea.bffa.objects.PlayingPlayer;

public class Main extends JavaPlugin {
   public static ArrayList<Material> materials = new ArrayList<>();
   public static Main instance;
   public static Location bffaloc = null;
   public static FileConfiguration cfg;
   public static int combatlog = 8;
   public static int blockdisappearing = 0;
   public static int blockappearing = 1;
   public static int voidheight = 40;
   public static int maxy = 0;

   public static void runBukkitTask(final Runnable e, long time) {
      (new BukkitRunnable() {
         public void run() {
            e.run();
         }
      }).runTaskLater(instance, time);
   }

   public void enable() {
      this.saveDefaultConfig();
      cfg = this.getConfig();
      combatlog = this.getConfig().getInt("combatlog");
      blockdisappearing = this.getConfig().getInt("block-disappearing");
      maxy = this.getConfig().getInt("maxbuildy");
      blockappearing = this.getConfig().getInt("block-appearing");
      voidheight = this.getConfig().getInt("void-height");

      try {
         bffaloc = (Location)cfg.get("spawnloc");
      } catch (Exception var2) {
         var2.printStackTrace();
      }

      materials.add(Material.GOLDEN_APPLE);
      materials.add(Material.FIREBALL);
      materials.add(Material.ENDER_PEARL);
      materials.add(Material.POTION);
      this.getServer().getPluginManager().registerEvents(new BuildFFA(), this);
      this.getServer().getPluginManager().registerEvents(new KitsGUI(), this);
      this.getServer().getPluginManager().registerEvents(new KillsAndDeaths(), this);
      this.getServer().getPluginManager().registerEvents(new Blocking(), this);
      this.getServer().getPluginManager().registerEvents(new CommandsListener(), this);
      this.getServer().getPluginManager().registerEvents(new Fireball(), this);
      this.getServer().getPluginCommand("buildffa").setExecutor(new BuildFFACommand());
      (new PAPIExpansion()).register();
      Kit.loadKits();
      Cache.load();
      Potions.load();
      KillReward.load();
   }

   public void onEnable() {
      super.onEnable();
      instance = this;
      this.enable();
   }

   public void onDisable() {
      super.onDisable();
      Cache.save();
      for (PlayingPlayer playingPlayer : PlayingPlayer.instances) {
         Cache.setStats(playingPlayer);
      }
   }

   public static boolean leaveifArena(Player p) {
      IArena arena = Arena.getArenaByPlayer(p);
      if (arena == null) {
         return false;
      } else {
         if (arena.isPlayer(p)) {
            arena.removePlayer(p, false);
         } else if (arena.isSpectator(p)) {
            arena.removeSpectator(p, false);
         }

         return true;
      }
   }

   public static int randomInt(Integer min, Integer max) {
      Random random = new Random();
      return random.nextInt(max - min + 1) + min;
   }
}
