package de.freemine.permissioneffects;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * @author LPkkjHD
 */
public class MainCommand implements CommandExecutor {
    private Main main;

    public MainCommand(Main main) {
        this.main = main;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("PermissionEffects")) {
            if (sender.hasPermission("pe.admin")) {
                if (args.length == 0) {
                    sender.sendMessage(header("Permission Effects"));
                    sender.sendMessage(" ");
                    sender.sendMessage("§a/pe reload          §2#Reloads the Effects");
                    sender.sendMessage(" ");
                    sender.sendMessage(footer());
                } else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("reload")) {
                        try {
                            ReloadPermissionEffects();
                            sender.sendMessage("§8§l[§7P§6E§8§l] §aReload complete");
                        } catch (Exception e) {
                            sender.sendMessage("§4ERROR: §cFailed to reload the PermissionEffects");
                            e.printStackTrace();
                        }
                    } else if (args[0].equalsIgnoreCase("list")) {
                        sender.sendMessage(header("Effects"));
                        for (PotionEffect effect : PotionEffect.values()) {
                            sender.sendMessage("§8§l[§7P§6E§8§l]§r " + effect.name().toLowerCase());
                        }
                        sender.sendMessage(footer());
                    }
                } else {
                    sender.sendMessage("§cYou just gave me too many arguments");
                }
            } else {
                return true;
            }
        }
        return false;
    }

    private void ReloadPermissionEffects() {

        //Clearing all effects
        for (Player player : main.getServer().getOnlinePlayers()) {
            for (PotionEffect types : PotionEffect.values()) {
                player.removePotionEffect(PotionEffectType.getByName(types.name()));
            }
        }

        //setting the new Values
        for (Player player : main.getServer().getOnlinePlayers()) {
            Util.addEffects(player);
        }
    }

    private String header(String s) {
        return "§b█████████§9[ §3" + s + " §9]§b██████████";
    }

    private String footer() {
        return "§b███████████████████████████████";
    }
}
