package dev.ftb.mods.ftbobsidian;

import dev.ftb.mods.ftblibrary.snbt.config.ConfigUtil;
import dev.ftb.mods.ftbobsidian.client.ObsidianClient;
import dev.ftb.mods.ftbobsidian.config.StartupConfig;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;

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

    public Obsidian() {
        ObsidianClient.init();

        ConfigUtil.loadDefaulted(StartupConfig.CONFIG, ConfigUtil.CONFIG_DIR, MOD_ID);
//        ConfigManager.getInstance().registerServerConfig(ServerConfig.CONFIG, MOD_ID + ".client", false); // Server.
//        ConfigManager.getInstance().registerServerConfig(CommonConfig.CONFIG, MOD_ID + ".client", true); // Common

        var eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        if (FMLEnvironment.dist == Dist.CLIENT) {
            eventBus.<FMLClientSetupEvent>addListener(event -> clientSetup(event, eventBus));
        }

        eventBus.addListener(this::addPackFinders);
    }

    public void addPackFinders(AddPackFindersEvent event) {
        PackType packType = event.getPackType();
        if (packType == PackType.SERVER_DATA) {
            var packSource = PackSource.create(type -> Component.translatable("pack.nameAndSource", type, OBSIDIAN_LOADED_DECORATOR), true);
            event.addRepositorySource(new FolderRepositorySource(DATA_PACKS_PATH, PackType.SERVER_DATA, packSource));
        }
    }

    private void clientSetup(FMLClientSetupEvent event, IEventBus eventBus) {
        ObsidianClient.setup();
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(Obsidian.MOD_ID, path);
    }
}
