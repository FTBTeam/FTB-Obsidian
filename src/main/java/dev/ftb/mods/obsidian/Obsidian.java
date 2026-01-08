package dev.ftb.mods.obsidian;

import dev.ftb.mods.ftblibrary.config.manager.ConfigManager;
import dev.ftb.mods.obsidian.client.ObsidianClient;
import dev.ftb.mods.obsidian.config.CommonConfig;
import dev.ftb.mods.obsidian.config.ServerConfig;
import dev.ftb.mods.obsidian.pack.PathBasedPackSource;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;

import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

@Mod(Obsidian.MOD_ID)
public class Obsidian {
    public static final String MOD_ID = "ftbobsidian";

    public static Path FTB_DIRECTORY = FMLPaths.GAMEDIR.get().resolve("obsidian");

    private static final Logger LOGGER = LoggerFactory.getLogger(Obsidian.class);

    public Obsidian(IEventBus eventBus, ModContainer container) {
        ObsidianClient.init();

        ConfigManager.getInstance().registerServerConfig(ServerConfig.CONFIG, MOD_ID + ".client", false); // Server.
        ConfigManager.getInstance().registerServerConfig(CommonConfig.CONFIG, MOD_ID + ".client", true); // Common

        if (FMLEnvironment.dist == Dist.CLIENT) {
            eventBus.<FMLClientSetupEvent>addListener(event -> clientSetup(event, eventBus));
        }

        eventBus.addListener(this::addPackFinders);
    }

    public void addPackFinders(AddPackFindersEvent event) {
        PackType packType = event.getPackType();
        event.addRepositorySource(new PathBasedPackSource(packType));
    }

    private void clientSetup(FMLClientSetupEvent event, IEventBus eventBus) {
        ObsidianClient.setup();
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(Obsidian.MOD_ID, path);
    }
}
