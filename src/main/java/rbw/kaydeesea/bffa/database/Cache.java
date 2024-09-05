package rbw.kaydeesea.bffa.database;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import rbw.kaydeesea.bffa.objects.PlayingPlayer;

public class Cache {
   static File f;
   static FileConfiguration db;
   static String kills = ".kills";
   static String deaths = ".deaths";
   static String ks = ".killsstreak";
   static String ds = ".deathsstreak";
   static String highestks = ".highestks";
   static String highestds = ".highestds";

   public static void load() {
      f = new File("plugins/BuildFFA", "db.yml");
      db = YamlConfiguration.loadConfiguration(f);
   }

   public static void updateMaxes(PlayingPlayer pe) {
      FileConfiguration var10000 = db;
      String var10001 = pe.getPlayer().getName();
      int d;
      if (var10000.isSet(var10001 + highestks)) {
         var10000 = db;
         var10001 = pe.getPlayer().getName();
         d = var10000.getInt(var10001 + highestks);
         if (d < pe.getKillStreak()) {
            db.set(pe.getPlayer().getName() + highestks, pe.getKillStreak());
         }
      } else {
         db.set(pe.getPlayer().getName() + highestks, pe.getKillStreak());
      }

      var10000 = db;
      var10001 = pe.getPlayer().getName();
      if (var10000.isSet(var10001 + highestds)) {
         var10000 = db;
         var10001 = pe.getPlayer().getName();
         d = var10000.getInt(var10001 + highestds);
         if (d < pe.getDeathStreak()) {
            db.set(pe.getPlayer().getName() + highestds, pe.getDeathStreak());
         }
      } else {
         db.set(pe.getPlayer().getName() + highestds, pe.getDeathStreak());
      }

   }

   public static void setStats(PlayingPlayer pe) {
      db.set(pe.getPlayer().getName() + kills, pe.getKills());
      db.set(pe.getPlayer().getName() + deaths, pe.getDeaths());
      updateMaxes(pe);
   }

   public static void reset(String p) {
      Cache.Stats[] var1 = Cache.Stats.values();

       for (Stats stats : var1) {
           if (stats.equals(Stats.KILLS)) {
               db.set(p + kills, 0);
           }

           if (stats.equals(Stats.DEATHS)) {
               db.set(p + deaths, 0);
           }

           if (stats.equals(Stats.KS)) {
               db.set(p + ks, 0);
           }

           if (stats.equals(Stats.DS)) {
               db.set(p + ds, 0);
           }

           if (stats.equals(Stats.HIGHESTKS)) {
               db.set(p + highestks, 0);
           }

           if (stats.equals(Stats.HIGHESTDS)) {
               db.set(p + highestds, 0);
           }
       }

   }

   public static int getStats(String p, Cache.Stats stats) {
      if (!db.isInt(p + kills)) {
         return 0;
      } else if (stats.equals(Cache.Stats.KILLS)) {
         return db.getInt(p + kills);
      } else if (stats.equals(Cache.Stats.DEATHS)) {
         return db.getInt(p + deaths);
      } else if (stats.equals(Cache.Stats.KS)) {
         return db.getInt(p + ks);
      } else if (stats.equals(Cache.Stats.DS)) {
         return db.getInt(p + ds);
      } else if (stats.equals(Cache.Stats.HIGHESTKS)) {
         return db.getInt(p + highestks);
      } else {
         return stats.equals(Cache.Stats.HIGHESTDS) ? db.getInt(p + highestds) : 0;
      }
   }

   public static void save() {
      try {
         db.save(f);
      } catch (IOException var1) {
         var1.printStackTrace();
      }

   }

   public static List<String> getPlayersLB(Cache.Stats stat) {
      List<String> s = new ArrayList<>(db.getKeys(false));
      s.sort((a, b) -> getStats(b, stat) - getStats(a, stat));
      return s;
   }

   public enum Stats {
      KILLS,
      DEATHS,
      HIGHESTKS,
      KS,
      HIGHESTDS,
      DS;
   }
}
