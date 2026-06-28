package dev.ftb.mods.ftbobsidian.defaults;

import dev.ftb.mods.ftbobsidian.Obsidian;

import com.google.gson.Gson;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultsSync {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultsSync.class);
    private static final Gson GSON = new Gson().newBuilder().setPrettyPrinting().create();

    private static final Path LOGS_DIRECTORY = FMLPaths.GAMEDIR.get().resolve("logs");

    private static final Path DEFAULTS_DIRECTORY = Obsidian.FTB_DIRECTORY.resolve("defaults");
    private static final Path MEMORY_FILE = DEFAULTS_DIRECTORY.resolve("memory.json");

    // This is the "default/root" directory that will always be attempted to sync on the first run.
    // We're using `all` here to reserve the defaults directory for potential versioned defaults.
    private static final Path ALL_DIRECTORY = DEFAULTS_DIRECTORY.resolve("all");

    /**
     * This is injected into the `main` class for both the server & client at the very top of the main method.
     * This is to allow us to make any needed modifications to the game before ANYTHING has a chance to load.
     */
    public static void run() {
        // No actions to run.
        if (!Files.exists(ALL_DIRECTORY)) {
            return;
        }

        var memoryData = loadMemoryData();
        if (memoryData.defaultsSynced()) {
            LOGGER.info("Defaults have already been synced, skipping.");
            return;
        }

        syncDirectory(ALL_DIRECTORY);
    }

    /**
     * Syncs the contents of the source file to the directory folder recursively. If the file already exists, it will
     * be ignored, and if it does not exist, it will be copied over.
     *
     * @param sourceDirectory The source directory to sync from.
     */
    private static void syncDirectory(final Path sourceDirectory) {
        var target = FMLPaths.GAMEDIR.get();

        var log = new AtomicReference<>("");

        try {
            Files.walkFileTree(sourceDirectory, new NonOverwritingVisitor(target, sourceDirectory, (msg) -> log.getAndUpdate(s -> s + msg + "\n")));
            LOGGER.info("Successfully synced defaults from {} to {}", sourceDirectory, target);

            var memoryData = new MemoryData(true);
            saveMemoryData(memoryData);
        } catch (Exception e) {
            LOGGER.error("Failed to sync defaults from {} to {}", sourceDirectory, target, e);
        }

        try {
            Files.writeString(LOGS_DIRECTORY.resolve("defaults-sync.log"), log.get());
        } catch (IOException e) {
            LOGGER.error("Failed to write defaults sync log to {}", LOGS_DIRECTORY.resolve("defaults-sync.log"), e);
        }
    }

    /**
     * Loads the memory data from the memory file. If the file does not exist or fails to load, a new MemoryData instance is created with defaultsSynced set to false.
     * @return The loaded or newly created MemoryData instance.
     */
    private static MemoryData loadMemoryData() {
        if (Files.exists(MEMORY_FILE)) {
            try {
                var json = Files.readString(MEMORY_FILE);
                return GSON.fromJson(json, MemoryData.class);
            } catch (Exception e) {
                LOGGER.error("Failed to load memory data from {}", MEMORY_FILE, e);
            }
        }

        return new MemoryData(false);
    }

    /**
     * Saves the memory data to the memory file.
     * @param memoryData The memory data to save.
     */
    private static void saveMemoryData(MemoryData memoryData) {
        try {
            var json = GSON.toJson(memoryData);
            Files.writeString(MEMORY_FILE, json);
        } catch (Exception e) {
            LOGGER.error("Failed to save memory data to {}", MEMORY_FILE, e);
        }
    }
}
