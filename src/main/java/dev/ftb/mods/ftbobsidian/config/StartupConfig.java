package dev.ftb.mods.ftbobsidian.config;

import dev.ftb.mods.ftblibrary.snbt.config.SNBTConfig;
import dev.ftb.mods.ftblibrary.snbt.config.StringListValue;
import dev.ftb.mods.ftbobsidian.Obsidian;

import java.util.ArrayList;

public interface StartupConfig {
    SNBTConfig CONFIG = SNBTConfig.create(Obsidian.MOD_ID + "-startup");

    StringListValue FORCE_LOADED_RESOURCE_PACKS = CONFIG.addStringList("force_loaded_resource_packs", new ArrayList<>())
            .comment("List of force loaded resource packs to use, the resource pack must exist in the resourcepacks folder for it to be automatically enabled.");
}
