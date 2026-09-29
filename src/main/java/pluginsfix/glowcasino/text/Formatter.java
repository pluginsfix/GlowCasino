package pluginsfix.glowcasino.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class Formatter {

    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,###", DecimalFormatSymbols.getInstance(Locale.US));
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.##", DecimalFormatSymbols.getInstance(Locale.US));

    private Formatter() {
    }

    public static Component format(String text) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }
        String colorized = ChatColor.translateAlternateColorCodes('&', text);
        return LegacyComponentSerializer.legacySection()
                .deserialize(colorized)
                .decoration(TextDecoration.ITALIC, false);
    }

    public static String color(String text) {
        if (text == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String formatNumber(double number) {
        if (number == (long) number) {
            return NUMBER_FORMAT.format((long) number);
        }
        return DECIMAL_FORMAT.format(number);
    }

    public static String formatNumber(long number) {
        return NUMBER_FORMAT.format(number);
    }
}
