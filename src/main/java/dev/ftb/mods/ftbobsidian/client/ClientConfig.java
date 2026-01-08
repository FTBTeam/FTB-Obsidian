package dev.ftb.mods.ftbobsidian.client;

import dev.ftb.mods.ftblibrary.snbt.config.SNBTConfig;
import dev.ftb.mods.ftbobsidian.Obsidian;

public interface ClientConfig {
    SNBTConfig CONFIG = SNBTConfig.create(Obsidian.MOD_ID + "-client");

}
