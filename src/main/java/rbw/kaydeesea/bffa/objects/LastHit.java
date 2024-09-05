package rbw.kaydeesea.bffa.objects;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.bukkit.entity.Player;
import rbw.kaydeesea.bffa.Main;

public class LastHit {
   public static Map<Player, Timer> cooldowns = new HashMap<>();
   public static Map<Player, Player> lasthit = new HashMap<>();

   public static void setLasthit(final Player victim, Player hitplayer) {
      lasthit.put(victim, hitplayer);
      if (cooldowns.containsKey(victim)) {
         cooldowns.get(victim).cancel();
      }

      TimerTask task = new TimerTask() {
         public void run() {
            LastHit.cooldowns.remove(victim);
            LastHit.lasthit.remove(victim);
         }
      };
      Timer timer = new Timer("Timers-2345");
      timer.schedule(task, (long)Main.combatlog * 1000L);
      cooldowns.put(victim, timer);
   }

   public static Player getLasthit(Player victim) {
      return lasthit.get(victim);
   }
}
