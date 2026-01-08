package dev.ftb.mods.ftbobsidian.client;

import dev.ftb.mods.ftblibrary.config.manager.ConfigManager;
import dev.ftb.mods.ftbobsidian.Obsidian;

public class ObsidianClient {
    public static void init() {
        ConfigManager.getInstance().registerClientConfig(ClientConfig.CONFIG, Obsidian.MOD_ID + ".client");
    }

    public static void setup() {

    }
}
