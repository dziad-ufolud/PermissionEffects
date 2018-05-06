package de.freemine.permissioneffects.command;

import de.freemine.permissioneffects.Main;
import de.freemine.permissioneffects.util.PotionEffect;
import de.freemine.permissioneffects.util.SettingsFile;
import de.freemine.permissioneffects.util.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author LPkkjHD
 */
public class MainCommand extends PECommand {//TODO Tab-completion
    private Main main;

    public MainCommand(Main main) {
        super("PermissionEffects");
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("pe.admin")) {
            if (args.length == 0) {
                sender.sendMessage(header("Permission Effects"));
                sender.sendMessage(" ");
                sender.sendMessage("§a/pe reload          §2#Reloads the Effects");
                sender.sendMessage(" ");
                sender.sendMessage("§a/pe list          §2#lists the possible Effects");
                sender.sendMessage(" ");
                sender.sendMessage(footer());
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    try {
                        SettingsFile.init(); //Reload settings from disk
                        ReloadPermissionEffects(); //Reset effects on player
                        sender.sendMessage("§8§l[§7P§6E§8§l] §aReload complete");
                    } catch (Exception e) {
                        sender.sendMessage("§4ERROR: §cFailed to reload the PermissionEffects");
                        e.printStackTrace();
                    }
                } else if (args[0].equalsIgnoreCase("list")) {
                    sender.sendMessage(header("Effects"));
                    for (PotionEffect effect : PotionEffect.values()) {
                        sender.sendMessage("§8§l[§7P§6E§8§l]§r " + effect.toString());
                    }
                    sender.sendMessage(footer());
                }//TODO player reset command
            } else {
                sender.sendMessage("§cYou just gave me too many arguments");
            }
            return true;
        } else {
            return true;
        }
    }

    private void ReloadPermissionEffects() {

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
