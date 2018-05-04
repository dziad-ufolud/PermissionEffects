package de.freemine.permissioneffects.command;

import de.freemine.permissioneffects.util.PotionEffect;
import de.freemine.permissioneffects.util.SettingsFile;
import de.freemine.permissioneffects.util.Util;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.function.Consumer;

public class ToggleCommand implements CommandExecutor {//TODO Tab-completion

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("pe.toggle")) {
            return true;
        }
        if (args.length == 0 && sender instanceof Player) { //TODO not player error message, help, tab-completion & toggle via command
            openMenu((Player) sender);
            return true;
        }
        return true;
    }

    private void openMenu(Player player) {
        Gui effectMenu = EffectMenu.create(player);
        effectMenu.open();
    }

    private static class EffectMenu extends Gui {
        private final ArrayList<PotionEffect> effects;

        public EffectMenu(Player player, int lines, ArrayList<PotionEffect> effects) {
            super(player, lines, "§8§l[§7P§6E§8§l] toggles");
            this.effects = effects;
        }

        public static EffectMenu create(Player player) {
            ArrayList<PotionEffect> ef = Util.getAllowedEffects(player);//TODO per potion toggle permissions
            // calculates the amount of lines required for an inventory of ef.size() potions
            int lines;
            if (ef.size() % 9 == 0) {
                lines = ef.size() / 9;
            } else {

                lines = ((ef.size() - (ef.size() % 9)) + 9) / 9;
            }
            return new EffectMenu(player, lines, ef);
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
            private Item item;

            public effectToggle(PotionEffect effect, Player p) {
                this.effect = effect;
                this.player = p;
                this.enabled = SettingsFile.isEffectEnabled(p, this.effect);

                org.bukkit.potion.PotionEffect bukkitEffect = new org.bukkit.potion.PotionEffect(PotionEffectType.getByName(effect.name()), 0, 0, false, false);

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
                        .lore("§rToggle the potion effect:§d" + bukkitEffect.getType().getName() + " " +
                                (this.enabled ? "§coff" : "§aon")
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
                        this.enabled = !this.enabled;
                        ItemStack itms = event.getCurrentItem();
                        ItemMeta meta = itms.getItemMeta();
                        SettingsFile.setEffect(this.player, this.effect, this.enabled);
                        meta.setDisplayName((this.enabled ? "§a" : "§c") + this.effect.toString());
                        meta.setLore(new ArrayList<>(Collections.singletonList("§rToggle the potion effect:§d" + bukkitEffect.getType().getName() + " " +
                                (this.enabled ? "§coff" : "§aon"))));
                        if (this.enabled) {
                            itms.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                        } else {
                            itms.getEnchantments().keySet().forEach(itms::removeEnchantment);
                        }
                        itms.setItemMeta(meta);
                        player.playSound(new Location(player.getWorld(), 0, 0, 0),
                                Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                                SoundCategory.BLOCKS,
                                1, 1
                        );
                        Util.updateEffect(this.player, this.effect);
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
