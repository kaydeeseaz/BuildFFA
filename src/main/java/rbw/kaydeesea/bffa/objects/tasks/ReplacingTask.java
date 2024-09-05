package rbw.kaydeesea.bffa.objects.tasks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import rbw.kaydeesea.bffa.Main;
import rbw.kaydeesea.bffa.listeners.Blocking;

public class ReplacingTask {
   public final Block b;
   public final Material oldtype;
   public final byte olddata;

   public ReplacingTask(Block b) {
      this.b = b;
      this.oldtype = b.getType();
      this.olddata = b.getData();
      this.run();
   }

   public void remove() {
      Blocking.replacingBlocks.remove(this.b);
   }

   private void run() {
      this.b.setType(Material.AIR);
      Main.runBukkitTask(() -> {
         this.b.setType(this.oldtype);
         this.b.setData(this.olddata);
         this.remove();
      }, (long)Main.blockappearing * 20L);
   }
}
