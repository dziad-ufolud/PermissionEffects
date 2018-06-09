package de.freemine.permissioneffects.command;

import de.freemine.permissioneffects.Reference;
import de.freemine.permissioneffects.file.SettingsFile;
import de.freemine.permissioneffects.util.PotionEffect;
import de.freemine.permissioneffects.util.Util;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class ToggleCommand extends PECommand {


    public ToggleCommand() {
        super("ToggleEffects");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("pe.toggle")) {
            sender.sendMessage(this.command.getPermissionMessage());
            return true;
        }
        if (args.length == 0 && sender instanceof Player) { //TODO not player error message, help & toggle via command
            openMenu((Player) sender);
            return true;
        } else if (args.length == 1) {
            sender.sendMessage(Reference.colouredPrefix + this.command.getUsage());
        } else if (args.length == 2) {
            if (sender instanceof Player) {
                String action = args[0];
                String effect = args[1];
                return doEffectChange(action, effect, (Player) sender);
            } else {
                sender.sendMessage(Reference.colouredPrefix + this.command.getUsage());
            }
        } else if (args.length == 3) {
            if (Util.hasChangeOther(sender)) {
                String action = args[0];
                String effect = args[1];
                Player other = sender.getServer().getPlayer(args[2]);
                return doRemoteEffectChange(action, effect, sender, other);
            } else {
                sender.sendMessage(Reference.colouredPrefix + ChatColor.DARK_RED + " You don't have permission" +
                        ChatColor.AQUA + "pe.other");
                sender.sendMessage(Reference.colouredPrefix + ChatColor.DARK_RED + " to change other peoples effects");
            }
        }
        return true;
    }

    private boolean doRemoteEffectChange(String action, String effect, CommandSender sender, Player other) {
        if (actionIsInvalidAndYell(action, sender)) {
            return true;
        }
        AtomicReference<PotionEffect> potionEffect = new AtomicReference<>();
        Arrays.asList(PotionEffect.values()).forEach(e -> {
            if (e.name().equalsIgnoreCase(effect)) {
                potionEffect.set(e);
            }
        });
        if (potionEffect.get() == null) {
            StringBuilder builder = new StringBuilder();
            Arrays.asList(PotionEffect.values()).forEach(e -> builder.append(e.name().toLowerCase()).append(","));
            if (builder.lastIndexOf(",") != -1) {
                builder.substring(0, builder.lastIndexOf(","));
            }
            sender.sendMessage(Reference.colouredPrefix + ChatColor.AQUA + effect + " is an invalid effect please use one of");
            sender.sendMessage(Reference.colouredPrefix + ChatColor.WHITE + builder);
            return true;
        }
        changeEffect(action, sender, other, potionEffect);
        return true;
    }

    private void changeEffect(String action, CommandSender sender, Player effected, AtomicReference<PotionEffect> potionEffect) {

        boolean endAction;

        switch (action.toLowerCase()) {
            case "toggle":
                endAction = !SettingsFile.isEffectEnabled(effected, potionEffect.get());
                break;
            case "enable":
                endAction = true;
                break;
            case "disable":
                endAction = false;
                break;
            default:
                sender.sendMessage(Reference.colouredPrefix + ChatColor.RED + "Invalid action please use toggle/enable/disable");
                sender.sendMessage(Reference.colouredPrefix + this.command.getUsage());
                return;
        }

        if (sender instanceof Player && ((Player) sender).getUniqueId().equals(effected.getUniqueId())) {
            sender.sendMessage(Reference.colouredPrefix + " turning " + potionEffect.get().toString() +
                    (endAction ? ChatColor.GREEN + " on" : ChatColor.RED + " off"));
        } else {
            sender.sendMessage(Reference.colouredPrefix + " turning " + potionEffect.get().toString() +
                    (endAction ? ChatColor.GREEN + " on" : ChatColor.RED + " off") + " for " + effected.getDisplayName());
        }
        SettingsFile.setEffect(effected, potionEffect.get(), endAction);
        Util.updateEffect(effected, potionEffect.get());
    }

    private boolean doEffectChange(String action, String effect, Player sender) {
        if (actionIsInvalidAndYell(action, sender)) {
            return true;
        }
        AtomicReference<PotionEffect> potionEffect = new AtomicReference<>();
        Util.getAllowedEffects(sender).forEach(e -> {
            if (e.name().equalsIgnoreCase(effect) && Util.hasToggleEffectPermission(sender, e)) {
                potionEffect.set(e);
            }
        });
        if (potionEffect.get() == null) {
            StringBuilder builder = new StringBuilder();
            Util.getAllowedEffects(sender).forEach(e -> {
                if (Util.hasToggleEffectPermission(sender, e)) {
                    builder.append(e.name().toLowerCase()).append(",");
                }
            });
            if (builder.lastIndexOf(",") != -1) {
                builder.substring(0, builder.lastIndexOf(","));
            }
            sender.sendMessage(Reference.colouredPrefix + ChatColor.AQUA + effect + " is an invalid effect please use one of");
            sender.sendMessage(Reference.colouredPrefix + ChatColor.WHITE + builder);
            return true;
        }
        changeEffect(action, sender, sender, potionEffect);
        return true;
    }

    private boolean actionIsInvalidAndYell(String action, CommandSender sender) {
        if (!("toggle".equalsIgnoreCase(action) || "enable".equalsIgnoreCase(action) || "disable".equalsIgnoreCase(action))) {
            sender.sendMessage(Reference.colouredPrefix + ChatColor.RED + "Invalid action please use toggle/enable/disable");
            sender.sendMessage(Reference.colouredPrefix + this.command.getUsage());
            return true;
        } else {
            return false;
        }
    }

    private void openMenu(Player player) {
        Gui effectMenu = EffectMenu.create(player);
        effectMenu.open();
    }

    @Override
    public ArrayList<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> completions = new ArrayList<>();
        Player p;
        if (!(sender instanceof Player)) {
            return completions;//TODO make console version
        } else {
            p = (Player) sender;
        }
        if (args.length == 0) {
            completions.add("toggle");
            completions.add("enable");
            completions.add("disable");

        } else if (args.length == 1) {
            if ("toggle".startsWith(args[0])) {
                completions.add("toggle");
            }
            if ("disable".startsWith(args[0])) {
                completions.add("disable");
            }
            if ("enable".startsWith(args[0])) {
                completions.add("enable");
            }

        } else if (args.length == 2) {
            for (PotionEffect effect : Util.getAllowedEffects(p)) {
                if (effect.name().toLowerCase().startsWith(args[1].toLowerCase()) && Util.hasToggleEffectPermission(p, effect)) {
                    completions.add(effect.name().toLowerCase());
                }
            }
        }//TODO add other player option
        return completions;
    }

    private static class EffectMenu extends Gui {
        private final ArrayList<PotionEffect> effects;

        private EffectMenu(Player player, int lines, ArrayList<PotionEffect> effects) {
            super(player, lines, Reference.colouredPrefix + " toggles");
            this.effects = effects;
        }

        static EffectMenu create(Player player) {
            ArrayList<PotionEffect> aef = new ArrayList<>();
            Util.getAllowedEffects(player).forEach(e -> {
                if (Util.hasToggleEffectPermission(player, e)) {
                    aef.add(e);
                }
            });
            // calculates the amount of lines required for an inventory of aef.size() potions
            int lines;
            if (aef.size() % 9 == 0) {
                lines = aef.size() / 9;
            } else {

                lines = ((aef.size() - (aef.size() % 9)) + 9) / 9;
            }
            return new EffectMenu(player, lines, aef);
        }

        @Override
        public void redraw() {
            for (PotionEffect effect : this.effects) {
                this.addItem(new effectToggle(effect, this.getPlayer()).getItem());
            }
        }

        private static class effectToggle {

            private final PotionEffect effect;
            private final Player player;
            private Boolean enabled;
            private final Item item;

            public effectToggle(PotionEffect effect, Player p) {
                this.effect = effect;
                this.player = p;
                this.enabled = SettingsFile.isEffectEnabled(p, this.effect);

                org.bukkit.potion.PotionEffect bukkitEffect = new org.bukkit.potion.PotionEffect(PotionEffectType.getByName(effect.name()), 0, 0, false, false);


                //TODO replace this item creation with reading from config file
                ItemStackBuilder builder = ItemStackBuilder.of(Material.POTION).amount(1)
                        .transformMeta(m -> {
                            PotionType type = null;
                            for (PotionType type1 : PotionType.values()) {
                                if (bukkitEffect.getType().equals(type1.getEffectType())) {
                                    type = type1;
                                    break;
                                }
                            }
                            if (type == null) {
                                type = PotionType.AWKWARD;
                            }

                            ((PotionMeta) m).setBasePotionData(new PotionData(type, false, false));
                            ((PotionMeta) m).clearCustomEffects();
                            ((PotionMeta) m).addCustomEffect(bukkitEffect, true);
                        })
                        .name((this.enabled ? "§a" : "§c") + this.effect.toString())
                        .lore("§rToggle the potion effect:§d" + bukkitEffect.getType().getName() +
                                (this.enabled ? "§c off" : "§a on")
                        );

                if (this.enabled) {
                    builder.enchant(Enchantment.DURABILITY);
                }

                ItemStack item = builder.build();
                HashMap<ClickType, Consumer<InventoryClickEvent>> handlers = new HashMap<>();
                for (ClickType type : ClickType.values()) {
                    // change the state of this object
                    // change the name color and lore of this object
                    // remove or add enchantment based on state
                    // Go ding when clicked
                    handlers.put(type, event -> {
                        if (Util.hasToggleEffectPermission(this.player, this.effect)) {
                            this.enabled = !this.enabled;
                            ItemStack itemStack = event.getCurrentItem();
                            ItemMeta meta = itemStack.getItemMeta();
                            SettingsFile.setEffect(this.player, this.effect, this.enabled);
                            meta.setDisplayName((this.enabled ? "§a" : "§c") + this.effect.toString());
                            meta.setLore(new ArrayList<>(Collections.singletonList("§rToggle the potion effect:§d" + this.effect.toString() +
                                    (this.enabled ? "§c off" : "§a on"))));
                            if (this.enabled) {
                                itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                            } else {
                                itemStack.getEnchantments().keySet().forEach(itemStack::removeEnchantment);
                            }
                            itemStack.setItemMeta(meta);
                            player.playSound(new Location(player.getWorld(), 0, 0, 0),
                                    Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                                    SoundCategory.BLOCKS,
                                    1, 1
                            );
                            Util.updateEffect(this.player, this.effect);
                        } else {
                            // if player doesn't have the permission to toggle the effect
                            // remove it from the menu when he tries to click it
                            this.getItem().getItemStack().setType(Material.AIR);
                        }
                    });
                }
                this.item = new Item(handlers, item);
            }

            public Item getItem() {
                return this.item;
            }


        }

    }
}
