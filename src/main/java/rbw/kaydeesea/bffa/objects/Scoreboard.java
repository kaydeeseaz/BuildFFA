package rbw.kaydeesea.bffa.objects;

import com.andrei1058.bedwars.sidebar.BedWarsScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.ScoreboardManager;
import rbw.kaydeesea.bffa.Main;

public class Scoreboard {
   static ScoreboardManager manager = Bukkit.getScoreboardManager();
   private final Player player;

   public Scoreboard(Player p) {
      this.player = p;
      PlayingPlayer pe = PlayingPlayer.getInstance(p);
      if (pe != null) {
         BedWarsScoreboard sb = BedWarsScoreboard.getSBoard(p.getUniqueId());
         if (sb != null) {
            sb.remove();
         }

         Bukkit.getServer().getScheduler().runTaskLater(Main.instance, () -> p.setScoreboard(this.getSB(pe)), 2L);
      }
   }

   public org.bukkit.scoreboard.Scoreboard getSB(PlayingPlayer pe) {
      org.bukkit.scoreboard.Scoreboard board = manager.getNewScoreboard();
      Objective objective = board.registerNewObjective("build", "ffa");
      objective.setDisplaySlot(DisplaySlot.SIDEBAR);
      objective.setDisplayName(Main.cfg.getString("scoreboard.scoreboard-name"));
      objective.getScore("§7§7§7").setScore(10);
      Score score9 = objective.getScore(Main.cfg.getString("scoreboard.top-kills").replaceAll("%player%", PlayingPlayer.getTopKills()));
      score9.setScore(9);
      objective.getScore("§7§7§7§8").setScore(8);
      Score score7 = objective.getScore(Main.cfg.getString("scoreboard.kills").replaceAll("%kills%", pe.getKills()+""));
      score7.setScore(7);
      Score score6 = objective.getScore(Main.cfg.getString("scoreboard.deaths").replaceAll("%deaths%", pe.getDeaths()+""));
      score6.setScore(6);
      objective.getScore("§7§7§7§9").setScore(5);
      Score score4 = objective.getScore(Main.cfg.getString("scoreboard.killstreak").replaceAll("%killstreak%", pe.getKillStreak()+""));
      score4.setScore(4);
      Score score3 = objective.getScore(Main.cfg.getString("scoreboard.deathstreak").replaceAll("%deathstreak%", pe.getDeathStreak()+""));
      score3.setScore(3);
      objective.getScore("§7§7§7§0").setScore(2);
      Score score1 = objective.getScore(Main.cfg.getString("scoreboard.ip"));
      score1.setScore(1);
      return board;
   }

   public void update() {
      PlayingPlayer pe = PlayingPlayer.getInstance(this.player);
      if (pe != null) {
         this.player.setScoreboard(this.getSB(pe));
      }
   }
}
