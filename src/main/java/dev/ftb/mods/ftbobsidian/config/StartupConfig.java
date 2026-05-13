package dev.ftb.mods.ftbobsidian.config;

import dev.ftb.mods.ftblibrary.snbt.config.SNBTConfig;
import dev.ftb.mods.ftblibrary.snbt.config.StringListValue;
import dev.ftb.mods.ftbobsidian.Obsidian;

import java.util.ArrayList;

public interface StartupConfig {
    SNBTConfig CONFIG = SNBTConfig.create(Obsidian.MOD_ID + "-startup");

    SNBTConfig PACKS = CONFIG.addGroup("packs");

    StringListValue FORCE_LOADED_RESOURCE_PACKS = PACKS.addStringList("force_loaded", new ArrayList<>())
            .comment("List of force loaded resource packs to use, the resource pack must exist in the resourcepacks folder for it to be automatically enabled.");

    StringListValue FORCED_COMPATIBLE_RESOURCE_PACKS = PACKS.addStringList("forced_compatible", new ArrayList<>())
            .comment("List of resource packs that are forced to be compatible with the current Minecraft version even if they would normally be marked as incompatible.");
}
