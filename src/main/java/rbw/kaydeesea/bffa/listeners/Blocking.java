package rbw.kaydeesea.bffa.listeners;

import java.util.HashMap;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import rbw.kaydeesea.bffa.Main;
import rbw.kaydeesea.bffa.objects.PlayingPlayer;
import rbw.kaydeesea.bffa.objects.tasks.BlockBreakTask;
import rbw.kaydeesea.bffa.objects.tasks.ReplacingTask;

public class Blocking implements Listener {
   public static HashMap<Player, Long> shootEnderPearls = new HashMap<>();
   public static HashMap<Block, BlockBreakTask> placedBlocks = new HashMap<>();
   public static HashMap<Block, ReplacingTask> replacingBlocks = new HashMap<>();

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.HIGH
   )
   public void onChat(AsyncPlayerChatEvent e) {
      if (e.getPlayer().getWorld().getName().equals(BuildFFA.bffaworld)) {
         e.setCancelled(true);
         PlayingPlayer p = PlayingPlayer.getInstance(e.getPlayer());
         if (p == null) {
            return;
         }

         if (e.getMessage().contains("$") || e.getMessage().contains("\\")) {
            return;
         }

         String pp = PlaceholderAPI.setPlaceholders(e.getPlayer(), Main.cfg.getString("messages.chat-formatting")).replaceAll("&", "§");
         BuildFFA.sendMessageInBffa(pp.replaceAll("%killsstreak%", String.valueOf(p.getKillStreak())).replaceAll("%player%", e.getPlayer().getName()).replaceAll("%message%", e.getMessage()));
      }

   }

   @EventHandler(
      priority = EventPriority.LOW,
      ignoreCancelled = true
   )
   public void onPlace(BlockPlaceEvent e) {
      PlayingPlayer pp = PlayingPlayer.getInstance(e.getPlayer());
      if (pp != null) {
         if (pp.isBuild()) {
            e.getPlayer().sendMessage("§aYou placed a §7" + e.getBlock().getType().toString() + " §ablock forever.");
            return;
         }

         if (e.getBlockPlaced().getY() > Main.maxy) {
            e.setCancelled(true);
            return;
         }

         Block b = e.getBlockPlaced();
         if (replacingBlocks.containsKey(b)) {
            return;
         }

         if (placedBlocks.containsKey(b)) {
            ((BlockBreakTask)placedBlocks.get(b)).cancel();
         }

         Material mat = e.getPlayer().getItemInHand().clone().getType();
         placedBlocks.put(b, new BlockBreakTask(b, mat, e.getPlayer()));
      }

   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.LOW
   )
   public void onBreak(BlockBreakEvent e) {
      PlayingPlayer pp = PlayingPlayer.getInstance(e.getPlayer());
      if (pp != null) {
         if (pp.isBuild()) {
            e.getPlayer().sendMessage("§aYou broke a §7" + e.getBlock().getType().toString() + " §ablock forever.");
            return;
         }

         if (e.getBlock().getY() > Main.maxy) {
            e.setCancelled(true);
            return;
         }

         Block b = e.getBlock();
         if (placedBlocks.containsKey(b)) {
            ((BlockBreakTask)placedBlocks.get(b)).cancel();
            return;
         }

         if (replacingBlocks.containsKey(b)) {
            return;
         }

         replacingBlocks.put(b, new ReplacingTask(b));
      }

   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onCraft(CraftItemEvent e) {
      if (PlayingPlayer.isPlaying((Player)e.getWhoClicked())) {
         if (PlayingPlayer.getInstance((Player)e.getWhoClicked()).isBuild()) {
            return;
         }

         e.setCancelled(true);
      }

   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPlayerInteract(PlayerInteractEvent e) {
      if (e.getAction().toString().toLowerCase().contains("right_click") && e.getPlayer().getItemInHand() != null && e.getPlayer().getItemInHand().getType() == Material.ENDER_PEARL) {
         Player p = e.getPlayer();
         if (p.getGameMode() != GameMode.SURVIVAL) {
            return;
         }

         long now = System.currentTimeMillis();
         long lasthit = (Long)shootEnderPearls.getOrDefault(p, -1L);
         if (lasthit != -1L && now - lasthit < 1500L) {
            e.setCancelled(true);
            p.sendMessage("§4You are on enderpearl cooldown.");
            return;
         }

         shootEnderPearls.put(p, System.currentTimeMillis());
      }

   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onDrop(PlayerDropItemEvent e) {
      if (PlayingPlayer.isPlaying(e.getPlayer())) {
         e.setCancelled(true);
      }

   }
}
