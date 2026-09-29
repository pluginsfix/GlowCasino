package pluginsfix.glowcasino.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pluginsfix.glowcasino.service.CasinoService;
import pluginsfix.glowcasino.text.Messages;

import java.util.Collections;
import java.util.List;

public final class CasinoCommand implements CommandExecutor, TabCompleter {

    private final CasinoService service;
    private final Messages messages;

    public CasinoCommand(CasinoService service, Messages messages) {
        this.service = service;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("glowcasino.use")) {
            messages.send(sender, "errors.no-permission");
            return true;
        }

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                messages.send(sender, "errors.player-only");
                return true;
            }
            service.openMainMenu(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("glowcasino.admin.reload") && !sender.hasPermission("glowcasino.admin")) {
                messages.send(sender, "errors.no-permission");
                return true;
            }
            service.getConfig().reload();
            messages.reload();
            messages.send(sender, "errors.reloaded");
            return true;
        }

        messages.send(sender, "errors.unknown-command");
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            if ((sender.hasPermission("glowcasino.admin.reload") || sender.hasPermission("glowcasino.admin"))
                    && "reload".startsWith(args[0].toLowerCase())) {
                return List.of("reload");
            }
        }
        return Collections.emptyList();
    }
}
