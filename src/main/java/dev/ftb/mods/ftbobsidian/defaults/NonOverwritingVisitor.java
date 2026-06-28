package dev.ftb.mods.ftbobsidian.defaults;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Consumer;

/**
 * FileVisitor that will take a source directory, and a target directory and copy the contents of the source directory
 * to the target directory. The visitor will image the source directory over the target directory but will not
 * overwrite any existing files in the target directory.
 */
public class NonOverwritingVisitor implements FileVisitor<Path> {
    private final Path targetDirectory;
    private final Path sourceDirectory;

    private final Consumer<String> onAction;

    public NonOverwritingVisitor(Path target, Path source, Consumer<String> onAction) {
        this.targetDirectory = target;
        this.sourceDirectory = source;
        this.onAction = onAction;
    }

    @Override
    public @NotNull FileVisitResult preVisitDirectory(Path dir, @NotNull BasicFileAttributes attrs) throws IOException {
        var relativePath = sourceDirectory.relativize(dir);
        var targetPath = targetDirectory.resolve(relativePath);

        if (!Files.exists(targetPath)) {
            Files.createDirectories(targetPath);
            this.onAction.accept("Created directory: " + targetPath);
        }

        return FileVisitResult.CONTINUE;
    }

    /**
     * Only copies the file if it does not already exist in the target directory.
     */
    @Override
    public @NotNull FileVisitResult visitFile(Path file, @NotNull BasicFileAttributes attrs) throws IOException {
        // MacOS Go Brrr
        String fileName = file.getFileName().toString();
        if (fileName.equals(".DS_Store")) {
            return FileVisitResult.CONTINUE;
        }

        var relativePath = sourceDirectory.relativize(file);
        var targetPath = targetDirectory.resolve(relativePath);

        if (!Files.exists(targetPath)) {
            Files.copy(file, targetPath);
            this.onAction.accept("Copied file from " + file + " to " + targetPath);
        }

        return FileVisitResult.CONTINUE;
    }

    @Override
    public @NotNull FileVisitResult visitFileFailed(Path file, @NotNull IOException exc) {
        // Don't let one failed file abort the whole walk; continue to the rest.
        return FileVisitResult.CONTINUE;
    }

    @Override
    public @NotNull FileVisitResult postVisitDirectory(Path dir, @Nullable IOException exc) throws IOException {
        if (exc != null) {
            throw exc;
        }

        return FileVisitResult.CONTINUE;
    }
}
