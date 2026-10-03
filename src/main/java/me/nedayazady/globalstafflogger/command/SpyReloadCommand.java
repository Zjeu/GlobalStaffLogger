package me.nedayazady.globalstafflogger.command;

import com.velocitypowered.api.command.SimpleCommand;
import me.nedayazady.globalstafflogger.config.ConfigManager;
import me.nedayazady.globalstafflogger.utils.ColorUtils;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.List;

public class SpyReloadCommand implements SimpleCommand {

    private final ConfigManager configManager;
    private final Logger logger;

    public SpyReloadCommand(ConfigManager configManager, Logger logger) {
        this.configManager = configManager;
        this.logger = logger;
    }

    @Override
    public void execute(Invocation invocation) {
        if (!hasPermission(invocation)) {
            String prefix = configManager.getMessage("prefix");
            String noPerm = configManager.getMessage("no-permission");
            if (noPerm.isEmpty()) {
                noPerm = "&cYou do not have permission to use this command.";
            }
            invocation.source().sendMessage(ColorUtils.parse(prefix + noPerm));
            return;
        }

        boolean success = configManager.reloadConfigs();
        String prefix = configManager.getMessage("prefix");

        if (success) {
            String successMsg = configManager.getMessage("reload-success");
            if (successMsg.isEmpty()) {
                successMsg = "&aConfiguration and messages have been reloaded successfully.";
            }
            invocation.source().sendMessage(ColorUtils.parse(prefix + successMsg));
            if (logger != null) {
                logger.info(configManager.getConsoleLogPrefix() + " Configuration and messages have been reloaded.");
            }
        } else {
            String failMsg = configManager.getMessage("reload-fail");
            if (failMsg.isEmpty()) {
                failMsg = "&cFailed to reload configuration. Please check console logs.";
            }
            invocation.source().sendMessage(ColorUtils.parse(prefix + failMsg));
            if (logger != null) {
                logger.error(configManager.getConsoleLogPrefix() + " Failed to reload configuration.");
            }
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("spy.nedayazady.reload") ||
               invocation.source().hasPermission("nedayazady.spy.reload");
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return Collections.emptyList();
    }
}
