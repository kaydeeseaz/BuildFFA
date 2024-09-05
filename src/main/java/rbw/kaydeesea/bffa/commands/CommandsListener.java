package rbw.kaydeesea.bffa.commands;

import com.andrei1058.bedwars.listeners.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import rbw.kaydeesea.bffa.objects.PlayingPlayer;

public class CommandsListener implements Listener {
   @EventHandler(
      ignoreCancelled = true
   )
   public void onCommand(PlayerCommandPreprocessEvent e) {
      if (PlayingPlayer.isPlaying(e.getPlayer())) {
         String command = e.getMessage().split(" ")[0].toLowerCase();
         if (!command.equals("/hub") && !command.equals("/lobby") && !command.equals("/leave") && !command.equals("/zoo") && !e.getMessage().startsWith("/bw leave")) {
            if (command.equals("/spawn")) {
               e.setCancelled(true);
               e.getPlayer().performCommand("buildffa spawn");
            }

            if (command.equals("/kits")) {
               e.setCancelled(true);
               e.getPlayer().performCommand("buildffa kits");
            }

         } else {
            Utils.handleLobbyJoin(e.getPlayer());
         }
      }
   }
}
