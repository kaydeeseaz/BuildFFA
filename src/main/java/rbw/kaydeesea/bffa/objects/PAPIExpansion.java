package rbw.kaydeesea.bffa.objects;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import rbw.kaydeesea.bffa.database.Cache;

public class PAPIExpansion extends PlaceholderExpansion {
   public @NotNull String getIdentifier() {
      return "buildffa";
   }

   public @NotNull String getAuthor() {
      return "KayDeeSea";
   }

   public @NotNull String getVersion() {
      return "1.0";
   }

   public String onPlaceholderRequest(Player player, @NotNull String params) {
      try {
         Cache.Stats.valueOf(params);
      } catch (IllegalArgumentException var4) {
         return "none";
      }

      return Cache.getStats(player.getName(), Cache.Stats.valueOf(params))+"";
   }
}
