package dev.ftb.mods.obsidian.config;

import dev.ftb.mods.ftblibrary.snbt.config.SNBTConfig;
import dev.ftb.mods.obsidian.Obsidian;

public interface CommonConfig {
    SNBTConfig CONFIG = SNBTConfig.create(Obsidian.MOD_ID + "-common");
}
