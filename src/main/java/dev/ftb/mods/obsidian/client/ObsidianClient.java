package dev.ftb.mods.obsidian.client;

import dev.ftb.mods.ftblibrary.config.manager.ConfigManager;
import dev.ftb.mods.obsidian.Obsidian;

public class ObsidianClient {
    public static void init() {
        ConfigManager.getInstance().registerClientConfig(ClientConfig.CONFIG, Obsidian.MOD_ID + ".client");
    }

    public static void setup() {

    }
}
