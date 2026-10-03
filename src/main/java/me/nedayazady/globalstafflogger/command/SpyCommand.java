package me.nedayazady.globalstafflogger.command;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import me.nedayazady.globalstafflogger.config.ConfigManager;
import me.nedayazady.globalstafflogger.manager.SpyManager;
import me.nedayazady.globalstafflogger.utils.ColorUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpyCommand implements SimpleCommand {

    private final SpyManager spyManager;
    private final ConfigManager configManager;

    public SpyCommand(SpyManager spyManager, ConfigManager configManager) {
        this.spyManager = spyManager;
        this.configManager = configManager;
    }

    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();

        if (args.length == 0) {
            sendMessage(invocation, configManager.getMessage("usage"));
            return;
        }

        String subCommand = args[0].toLowerCase();

        // Allow reload from console or players with permission
        if (subCommand.equals("reload")) {
            if (invocation.source().hasPermission("spy.nedayazady.reload") ||
                invocation.source().hasPermission("nedayazady.spy.reload")) {
                boolean success = configManager.reloadConfigs();
                if (success) {
                    String msg = configManager.getMessage("reload-success");
                    sendMessage(invocation, msg.isEmpty() ? "&aConfiguration and messages have been reloaded successfully." : msg);
                } else {
                    String msg = configManager.getMessage("reload-fail");
                    sendMessage(invocation, msg.isEmpty() ? "&cFailed to reload configuration. Please check console logs." : msg);
                }
            } else {
                sendMessage(invocation, configManager.getMessage("no-permission"));
            }
            return;
        }

        // Other subcommands are player-only
        if (!(invocation.source() instanceof Player)) {
            sendMessage(invocation, configManager.getMessage("only-players"));
            return;
        }

        Player player = (Player) invocation.source();
        boolean enabled = false;

        switch (subCommand) {
            case "chat":
                if (player.hasPermission("nedayazady.spy.chat")) {
                    spyManager.toggleChatSpy(player.getUniqueId());
                    enabled = spyManager.isChatSpyEnabled(player.getUniqueId());
                    sendStatusMessage(player, "Chat", enabled);
                } else {
                    sendMessage(player, configManager.getMessage("no-permission"));
                }
                break;
            case "cmd":
                if (player.hasPermission("nedayazady.spy.cmd")) {
                    spyManager.toggleCmdSpy(player.getUniqueId());
                    enabled = spyManager.isCmdSpyEnabled(player.getUniqueId());
                    sendStatusMessage(player, "Command", enabled);
                } else {
                    sendMessage(player, configManager.getMessage("no-permission"));
                }
                break;
            case "sw":
                if (player.hasPermission("nedayazady.spy.sw")) {
                    spyManager.toggleSwitchSpy(player.getUniqueId());
                    enabled = spyManager.isSwitchSpyEnabled(player.getUniqueId());
                    sendStatusMessage(player, "Switch", enabled);
                } else {
                    sendMessage(player, configManager.getMessage("no-permission"));
                }
                break;
            default:
                sendMessage(player, configManager.getMessage("usage"));
                break;
        }
    }

    private void sendStatusMessage(Player player, String type, boolean enabled) {
        String messageKey = enabled ? "spy-enabled" : "spy-disabled";
        String rawMessage = configManager.getMessage(messageKey).replace("{type}", type);
        sendMessage(player, rawMessage);
    }

    private void sendMessage(Invocation invocation, String message) {
        String prefix = configManager.getMessage("prefix");
        invocation.source().sendMessage(ColorUtils.parse(prefix + message));
    }

    private void sendMessage(Player player, String message) {
        String prefix = configManager.getMessage("prefix");
        player.sendMessage(ColorUtils.parse(prefix + message));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("nedayazady.spy.chat") ||
               invocation.source().hasPermission("nedayazady.spy.cmd") ||
               invocation.source().hasPermission("nedayazady.spy.sw") ||
               invocation.source().hasPermission("spy.nedayazady.reload") ||
               invocation.source().hasPermission("nedayazady.spy.reload");
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        if (args.length <= 1) {
            String current = args.length == 0 ? "" : args[0].toLowerCase();
            List<String> suggestions = new ArrayList<>();
            if (invocation.source().hasPermission("nedayazady.spy.chat") && "chat".startsWith(current)) {
                suggestions.add("chat");
            }
            if (invocation.source().hasPermission("nedayazady.spy.cmd") && "cmd".startsWith(current)) {
                suggestions.add("cmd");
            }
            if (invocation.source().hasPermission("nedayazady.spy.sw") && "sw".startsWith(current)) {
                suggestions.add("sw");
            }
            if ((invocation.source().hasPermission("spy.nedayazady.reload") ||
                 invocation.source().hasPermission("nedayazady.spy.reload")) && "reload".startsWith(current)) {
                suggestions.add("reload");
            }
            return suggestions;
        }
        return Collections.emptyList();
    }
}
