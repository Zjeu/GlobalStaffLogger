package me.nedayazady.globalstafflogger.config;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class ConfigManager {

    private final Path dataDirectory;
    private CommentedConfigurationNode configNode;
    private CommentedConfigurationNode messagesNode;

    public ConfigManager(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public void loadConfigs() {
        reloadConfigs();
    }

    public boolean reloadConfigs() {
        try {
            if (!Files.exists(dataDirectory)) {
                Files.createDirectories(dataDirectory);
            }

            CommentedConfigurationNode newConfig = loadConfig("config.yml");
            CommentedConfigurationNode newMessages = loadConfig("messages.yml");

            if (newConfig != null) {
                this.configNode = newConfig;
            }
            if (newMessages != null) {
                this.messagesNode = newMessages;
            }

            return newConfig != null && newMessages != null;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private CommentedConfigurationNode loadConfig(String fileName) {
        File file = new File(dataDirectory.toFile(), fileName);

        if (!file.exists()) {
            try (InputStream in = getClass().getResourceAsStream("/" + fileName)) {
                if (in != null) {
                    Files.copy(in, file.toPath());
                } else {
                    file.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(file.toPath())
                .build();

        try {
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Config Methods
    public List<String> getExcludedCommands() {
        if (configNode == null) {
            return Collections.emptyList();
        }
        try {
            return configNode.node("excluded-commands").getList(String.class, Collections.emptyList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public String getConsoleLogPrefix() {
        if (configNode == null) {
            return "[GlobalStaffLogger]";
        }
        String prefix = configNode.node("console-log-prefix").getString("[GlobalStaffLogger]");
        return prefix != null ? prefix : "[GlobalStaffLogger]";
    }

    // Messages Methods
    public String getMessage(String path) {
        if (messagesNode == null) {
            return "";
        }
        String msg = messagesNode.node((Object[]) path.split("\\.")).getString("");
        return msg != null ? msg : "";
    }
}
