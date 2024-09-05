package rbw.kaydeesea.bffa.objects.tasks;

import java.util.Random;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockBreakAnimation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import rbw.kaydeesea.bffa.Main;
import rbw.kaydeesea.bffa.listeners.Blocking;
import rbw.kaydeesea.bffa.objects.PlayingPlayer;

public class BlockBreakTask {
   private boolean cancelled = false;
   public final Block block;
   public Material toGive;
   public final Player player;
   public BukkitTask task = null;

   public static void addBreakingStatus(Player p, Block block, int destroystage) {
      PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation((new Random()).nextInt(2000), new BlockPosition(block.getX(), block.getY(), block.getZ()), destroystage);
      ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
   }

   public BlockBreakTask(Block b, Material toGive, Player player) {
      this.block = b;
      this.toGive = toGive;
      this.player = player;
      this.run();
   }

   private void run() {
      for (Player pla : Main.bffaloc.getWorld().getPlayers()) {
         this.autoInterval(Main.blockdisappearing * 20, this.block, pla, 1);
      }

      this.task = Bukkit.getScheduler().runTaskLater(Main.instance, () -> {
         if (this.block.getType() != Material.AIR) {
            if (PlayingPlayer.isPlaying(this.player)) {
               for (ItemStack itemStack : this.player.getInventory()) {
                  if (itemStack != null && itemStack.getType() == this.toGive) {
                     itemStack.setAmount(itemStack.getAmount() + 1);
                     break;
                  }
               }
            }

            this.block.setType(Material.AIR);
            this.remove();
         }
      }, (long)Main.blockdisappearing * 20L);

   }

   public void cancel() {
      this.task.cancel();
      this.cancelled = true;
      this.remove();
   }

   public void remove() {
      Blocking.placedBlocks.remove(this.block);
   }

   public void autoInterval(int ticks, Block b, Player p, int breaking) {
      if (!this.cancelled) {
         if (b.getType() != Material.AIR) {
            int a = ticks / 8;
            int[] as = new int[]{breaking};
            Main.runBukkitTask(() -> {
               addBreakingStatus(p, b, as[0]);
               as[0]++;
               if (as[0] <= 8) {
                  this.autoInterval(ticks, b, p, as[0]);
               }
            }, a);
         }
      }
   }
}
