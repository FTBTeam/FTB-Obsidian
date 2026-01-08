package dev.ftb.mods.ftbobsidian.config;

import dev.ftb.mods.ftblibrary.snbt.config.SNBTConfig;
import dev.ftb.mods.ftbobsidian.Obsidian;

public interface ServerConfig {
    SNBTConfig CONFIG = SNBTConfig.create(Obsidian.MOD_ID + "-client");
}
