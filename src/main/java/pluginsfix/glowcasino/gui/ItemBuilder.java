package pluginsfix.glowcasino.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import pluginsfix.glowcasino.text.Formatter;

import java.util.ArrayList;
import java.util.List;

public final class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(ItemStack item) {
        this.item = item.clone();
        this.meta = this.item.getItemMeta();
    }

    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder name(Component name) {
        if (meta != null) {
            meta.displayName(name.decoration(TextDecoration.ITALIC, false));
        }
        return this;
    }

    public ItemBuilder name(String name) {
        return name(Formatter.format(name));
    }

    public ItemBuilder lore(List<Component> lore) {
        if (meta != null && lore != null) {
            List<Component> cleanLore = new ArrayList<>(lore.size());
            for (Component line : lore) {
                cleanLore.add(line.decoration(TextDecoration.ITALIC, false));
            }
            meta.lore(cleanLore);
        }
        return this;
    }

    public ItemBuilder loreStrings(List<String> lore) {
        if (lore == null) return this;
        List<Component> components = new ArrayList<>(lore.size());
        for (String line : lore) {
            components.add(Formatter.format(line));
        }
        return lore(components);
    }

    public ItemBuilder leatherColor(Color color) {
        if (meta instanceof LeatherArmorMeta leatherMeta) {
            leatherMeta.setColor(color);
        }
        return this;
    }

    public ItemBuilder hideAllFlags() {
        if (meta != null) {
            meta.addItemFlags(ItemFlag.values());
        }
        return this;
    }

    public ItemStack build() {
        if (meta != null) {
            item.setItemMeta(meta);
        }
        return item;
    }
}
