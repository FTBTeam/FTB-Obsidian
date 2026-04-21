package dev.ftb.mods.ftbobsidian;

import dev.ftb.mods.ftblibrary.config.manager.ConfigManager;
import dev.ftb.mods.ftbobsidian.config.StartupConfig;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.level.validation.DirectoryValidator;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

@Mod(Obsidian.MOD_ID)
public class Obsidian {
    public static final String MOD_ID = "ftbobsidian";

    public static Path FTB_DIRECTORY = FMLPaths.GAMEDIR.get().resolve("obsidian");
    private static final Component OBSIDIAN_LOADED_DECORATOR = Component.literal("Obsidian").withStyle(ChatFormatting.DARK_PURPLE);
    private static final Path DATA_PACKS_PATH = FMLPaths.GAMEDIR.get().resolve("datapacks");

    private static final Logger LOGGER = LoggerFactory.getLogger(Obsidian.class);

    public Obsidian(IEventBus eventBus, ModContainer container, Dist dist) {
        ConfigManager.getInstance().registerStartupConfig(StartupConfig.CONFIG, MOD_ID);

        eventBus.addListener(this::addPackFinders);
    }

    public void addPackFinders(AddPackFindersEvent event) {
        PackType packType = event.getPackType();
        if (packType == PackType.SERVER_DATA) {
            var packSource = PackSource.create(type -> Component.translatable("pack.nameAndSource", type, OBSIDIAN_LOADED_DECORATOR), true);
            event.addRepositorySource(new FolderRepositorySource(DATA_PACKS_PATH, PackType.SERVER_DATA, packSource, new DirectoryValidator((path) -> true)));
        }
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(Obsidian.MOD_ID, path);
    }
}
