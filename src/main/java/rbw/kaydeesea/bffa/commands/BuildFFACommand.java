package rbw.kaydeesea.bffa.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rbw.kaydeesea.bffa.Main;
import rbw.kaydeesea.bffa.database.Cache;
import rbw.kaydeesea.bffa.guis.KitsGUI;
import rbw.kaydeesea.bffa.listeners.BuildFFA;
import rbw.kaydeesea.bffa.objects.LastHit;
import rbw.kaydeesea.bffa.objects.PlayingPlayer;

public class BuildFFACommand implements CommandExecutor {
   public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
      String cmd = args.length > 0 ? args[0] : "notsett";
      if (cmd.equals("notsett")) {
         commandSender.sendMessage("§3Plugin made by KayDeeSea#1783");
         commandSender.sendMessage("§9BuildFFA Help:");
         commandSender.sendMessage("§9");
         commandSender.sendMessage("§b/buildffa join");
         commandSender.sendMessage("§b/buildffa kits");
         commandSender.sendMessage("§b/buildffa spawn");
         commandSender.sendMessage("§b/buildffa leave");
         commandSender.sendMessage("§4/buildffa build");
         commandSender.sendMessage("§b/buildffa hotbarmanager");
         if (commandSender.hasPermission("op")) {
            commandSender.sendMessage("§4/buildffa reload");
            commandSender.sendMessage("§4/buildffa reset (player)");
         }

         commandSender.sendMessage("§9");
      } else {
         if (commandSender instanceof Player) {
            Player p = (Player)commandSender;
            PlayingPlayer pp;
            if (cmd.equals("join")) {
               pp = PlayingPlayer.getInstance(p);
               if (pp != null) {
                  p.sendMessage("§4You are already in.");
                  return true;
               }

               p.sendMessage("§2Joined §d§lBuild§5§lFFA!");
               if (Main.leaveifArena(p)) {
                  Main.runBukkitTask(() -> {
                     new PlayingPlayer(p);
                  }, 8L);
               } else {
                  new PlayingPlayer(p);
               }

               return true;
            }

            if (cmd.equals("leave")) {
               pp = PlayingPlayer.getInstance(p);
               if (pp == null) {
                  p.sendMessage("§4You are not in buildFFA.");
                  return true;
               }

               p.sendMessage("§cLeft §d§lBuild§5§lFFA");
               pp.remove(true);
               return true;
            }

            if (cmd.equals("reload") && p.hasPermission("op")) {
               commandSender.sendMessage("§aSuccessfully reloaded the config!");
               Main.instance.reloadConfig();
               return true;
            }

            if (cmd.equals("build") && p.hasPermission("op")) {
               if (!p.getWorld().getName().equals(BuildFFA.bffaworld)) {
                  p.sendMessage("§4You are not in buildFFA.");
                  return true;
               }

               pp = PlayingPlayer.getInstance(p);
               if (pp.isBuild()) {
                  pp.setBuild(false);
                  p.sendMessage("§4Toggled off §6Build Mode.");
               } else {
                  pp.setBuild(true);
                  p.sendMessage("§2Toggled on §6Build Mode.");
               }

               return true;
            }

            if (cmd.equals("kits")) {
               pp = PlayingPlayer.getInstance(p);
               if (pp == null) {
                  p.sendMessage("§4You are not in buildFFA.");
                  return true;
               }

               if (BuildFFA.isPlayerInSpawn(p)) {
                  KitsGUI.giveInv(p);
               } else {
                  p.sendMessage("§4You need to be in the spawn (/spawn).");
               }

               return true;
            }

            if (cmd.equals("reset")) {
               if (args.length != 2) {
                  p.sendMessage("§4Specify a valid player.");
                  return true;
               }

               String player = args[1];
               Cache.reset(player);
               p.sendMessage("§aSuccessfully reset player.");
            }

            if (cmd.equals("spawn")) {
               pp = PlayingPlayer.getInstance(p);
               if (pp == null) {
                  p.sendMessage("§4You are not in buildFFA.");
                  return true;
               }

               if (BuildFFA.isPlayerInSpawn(p)) {
                  p.sendMessage("§4You are already at spawn");
               } else {
                  if (LastHit.lasthit.containsValue(p) || LastHit.lasthit.containsKey(p)) {
                     p.sendMessage("§4You are combat logged with someone. Please wait a few seconds for the combat to expire.");
                     return true;
                  }

                  BuildFFA.tpToSpawn(p);
                  p.sendMessage("§aTeleported to spawn!");
               }

               return true;
            }
         } else if (cmd.equals("reload")) {
            Main.instance.reloadConfig();
            commandSender.sendMessage("§aDone!");
            return true;
         }

         commandSender.sendMessage("§3Plugin made by KayDeeSea#1783");
         commandSender.sendMessage("§9BuildFFA Help:");
         commandSender.sendMessage("§b/buildffa join");
         commandSender.sendMessage("§b/buildffa kits");
         commandSender.sendMessage("§b/buildffa spawn");
         commandSender.sendMessage("§b/buildffa leave");
         commandSender.sendMessage("§b/buildffa build");
         commandSender.sendMessage("§b/buildffa hotbarmanager");
         if (commandSender.hasPermission("op")) {
            commandSender.sendMessage("§4/buildffa reload");
         }

      }
       return true;
   }
}
