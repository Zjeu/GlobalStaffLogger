package me.nedayazady.globalstafflogger;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import me.nedayazady.globalstafflogger.command.SpyCommand;
import me.nedayazady.globalstafflogger.command.SpyReloadCommand;
import me.nedayazady.globalstafflogger.config.ConfigManager;
import me.nedayazady.globalstafflogger.listener.PlayerListener;
import me.nedayazady.globalstafflogger.manager.SpyManager;

import java.nio.file.Path;

@Plugin(
        id = "globalstafflogger",
        name = "GlobalStaffLogger",
        version = "1.2",
        description = "A high-performance logging plugin for the Velocity Proxy.",
        authors = {"nedayazady"}
)
public class GlobalStaffLogger {

    private final ProxyServer server;
    private final Logger logger;
    private final SpyManager spyManager;
    private final ConfigManager configManager;

    @Inject
    public GlobalStaffLogger(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.spyManager = new SpyManager();
        this.configManager = new ConfigManager(dataDirectory);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        configManager.loadConfigs();

        server.getEventManager().register(this, new PlayerListener(spyManager, server, configManager, logger));

        CommandMeta spyMeta = server.getCommandManager().metaBuilder("spy")
                .plugin(this)
                .build();
        server.getCommandManager().register(spyMeta, new SpyCommand(spyManager, configManager));

        CommandMeta spyReloadMeta = server.getCommandManager().metaBuilder("spyreload")
                .plugin(this)
                .build();
        server.getCommandManager().register(spyReloadMeta, new SpyReloadCommand(configManager, logger));
        
        logger.info("GlobalStaffLogger has been enabled!");
    }

    public ProxyServer getServer() {
        return server;
    }

    public Logger getLogger() {
        return logger;
    }
}
