package dev.ftb.mods.obsidian.pack;

import dev.ftb.mods.obsidian.Obsidian;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class PathBasedPackSource implements RepositorySource {
    private static final Logger LOGGER = LoggerFactory.getLogger(PathBasedPackSource.class);
    private static final Component OBSIDIAN_LOADED_DECORATOR = Component.literal("Obsidian").withStyle(ChatFormatting.DARK_PURPLE);

    private final Path path;
    private final PackType packType;

    public PathBasedPackSource(PackType packType) {
        this.packType = packType;
        this.path = Obsidian.FTB_DIRECTORY.resolve(packType == PackType.SERVER_DATA ? "data" : "assets");
    }

    public static void init() {
        try {
            var rootPath = getAndCreateIfNotExists(Obsidian.FTB_DIRECTORY);
            getAndCreateIfNotExists(rootPath.resolve("data"));
            getAndCreateIfNotExists(rootPath.resolve("assets"));
        } catch (IOException e) {
            LOGGER.error("Couldn't initialize VirtualPackSource", e);
        }
    }

    @Override
    public void loadPacks(@NotNull Consumer<Pack> consumer) {
        var packs = createPacksFromPath();
        if (packs.isEmpty()) {
            return;
        }

        for (var pack : packs) {
            LOGGER.debug("Loading pack {}", pack.location());
            consumer.accept(pack);
        }
    }

    private List<Pack> createPacksFromPath() {
        var visitor = new PackVisitor();
        try {
            Files.walkFileTree(this.path, visitor);
        } catch (IOException e) {
            LOGGER.error("Couldn't walk file tree", e);
            return new ArrayList<>();
        }

        List<Pack> packs = new ArrayList<>();
        List<Path> possiblePacks = visitor.getPackPaths();
        for (Path possiblePack : possiblePacks) {
            var name = possiblePack.getFileName().toString().replace(".zip", "").toLowerCase();

            PackLocationInfo locationInfo = new PackLocationInfo(
                    name,
                    Component.literal(name),
                    PackSource.create(packType -> Component.translatable("pack.nameAndSource", packType, OBSIDIAN_LOADED_DECORATOR), true),
                    Optional.empty()
            );

            Pack.ResourcesSupplier resourcesReader;
            if (Files.isDirectory(possiblePack)) {
                resourcesReader = new PathPackResources.PathResourcesSupplier(possiblePack);
            } else {
                resourcesReader = new FilePackResources.FileResourcesSupplier(possiblePack);
            }

            Pack.Metadata packInfo = Pack.readPackMetadata(locationInfo, resourcesReader, SharedConstants.getCurrentVersion().getPackVersion(this.packType));
            if (packInfo == null) {
                LOGGER.error("Couldn't read pack {}", possiblePack);
                continue;
            }

            PackSelectionConfig packSelectionConfig = new PackSelectionConfig(true, Pack.Position.TOP, false);
            packs.add(new Pack(locationInfo, resourcesReader, packInfo, packSelectionConfig));
        }

        return packs;
    }

    private static Path getAndCreateIfNotExists(Path path) throws IOException {
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }

        return path;
    }

    private static class PackVisitor implements FileVisitor<Path> {
        private final List<Path> packPaths = new ArrayList<>();

        @Override
        public @NotNull FileVisitResult preVisitDirectory(Path dir, @NotNull BasicFileAttributes attrs) {
            if (Files.exists(dir.resolve("pack.mcmeta"))) {
                this.packPaths.add(dir);
                // This is a pack, we can load it
                return FileVisitResult.SKIP_SUBTREE;
            }

            return FileVisitResult.CONTINUE;
        }

        @Override
        public @NotNull FileVisitResult visitFile(Path file, @NotNull BasicFileAttributes attrs) {
            if (file.getFileName().toString().equals("pack.mcmeta")) {
                // Check we don't already have this pack
                if (this.packPaths.stream().noneMatch(packPath -> packPath.resolve("pack.mcmeta").equals(file))) {
                    this.packPaths.add(file.getParent());
                }
            } else if (file.getFileName().toString().endsWith(".zip")) {
                // Check if we can load this zip file
                try (FileSystem fs = FileSystems.newFileSystem(file, Map.of("create", "false"))) {
                    if (Files.exists(fs.getPath("pack.mcmeta"))) {
                        // This is a pack, we can load it
                        this.packPaths.add(file);
                    }
                } catch (IOException e) {
                    LOGGER.error("Couldn't load zip file {}", file, e);
                }
            }

            return FileVisitResult.CONTINUE;
        }

        @Override
        public @NotNull FileVisitResult visitFileFailed(Path file, @NotNull IOException exc) {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public @NotNull FileVisitResult postVisitDirectory(Path dir, @Nullable IOException exc) {
            return FileVisitResult.CONTINUE;
        }

        public List<Path> getPackPaths() {
            return packPaths;
        }
    }
}
