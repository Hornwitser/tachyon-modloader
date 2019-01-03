package no.hornwitser.tachyon.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import tk.spgames.spaceteamserver.j;


@Mixin(j.class)
public abstract class MixinModelParser {
    private static Logger logger = LogManager.getLogger();

    @Redirect(method = "a(Ljava/io/File;)Lmoo/fC;", remap = false, at = @At(
        value = "NEW",
        target = "(Ljava/io/File;)Ljava/io/FileInputStream;",
        remap = false
    ))
    private static FileInputStream newFileInputStream1(File file)
    throws IOException {
        // logger.debug("Loading ship {}", file.getAbsolutePath());
        return Utils.newFileInputStream(file.getAbsolutePath());
    }

    @Redirect(method = "a(Ljava/io/File;)Lmoo/fC;", remap = false, at = @At(
        value = "NEW",
        target = "(Ljava/io/InputStream;)Ljava/io/InputStreamReader;",
        remap = false
    ))
    private static InputStreamReader newInputStreamReader1(InputStream stream)
    throws IOException {
        return Utils.newInputStreamReader(stream);
    }

    @Redirect(method = "b(Ljava/io/File;)Lmoo/eS;", remap = false, at = @At(
        value = "NEW",
        target = "(Ljava/io/File;)Ljava/io/FileInputStream;",
        remap = false
    ))
    private static FileInputStream newFileInputStream2(File file)
    throws IOException {
        // logger.debug("Loading system {}", file.getAbsolutePath());
        return Utils.newFileInputStream(file.getAbsolutePath());
    }

    @Redirect(method = "b(Ljava/io/File;)Lmoo/eS;", remap = false, at = @At(
        value = "NEW",
        target = "(Ljava/io/InputStream;)Ljava/io/InputStreamReader;",
        remap = false
    ))
    private static InputStreamReader newInputStreamReader2(InputStream stream)
    throws IOException {
        return Utils.newInputStreamReader(stream);
    }

    @Redirect(method = "c(Ljava/io/File;)Lmoo/aC;", remap = false, at = @At(
        value = "NEW",
        target = "(Ljava/io/File;)Ljava/io/FileInputStream;",
        remap = false
    ))
    private static FileInputStream newFileInputStream3(File file)
    throws IOException {
        // logger.debug("Loading ship projectile {}", file.getAbsolutePath());
        return Utils.newFileInputStream(file.getAbsolutePath());
    }

    @Redirect(method = "c(Ljava/io/File;)Lmoo/aC;", remap = false, at = @At(
        value = "NEW",
        target = "(Ljava/io/InputStream;)Ljava/io/InputStreamReader;",
        remap = false
    ))
    private static InputStreamReader newInputStreamReader3(InputStream stream)
    throws IOException {
        return Utils.newInputStreamReader(stream);
    }

    @Redirect(method = "d(Ljava/io/File;)Lmoo/az;", remap = false, at = @At(
        value = "NEW",
        target = "(Ljava/io/File;)Ljava/io/FileInputStream;",
        remap = false
    ))
    private static FileInputStream newFileInputStream4(File file)
    throws IOException {
        // logger.debug("Loading mob projectile {}", file.getAbsolutePath());
        return Utils.newFileInputStream(file.getAbsolutePath());
    }

    @Redirect(method = "d(Ljava/io/File;)Lmoo/az;", remap = false, at = @At(
        value = "NEW",
        target = "(Ljava/io/InputStream;)Ljava/io/InputStreamReader;",
        remap = false
    ))
    private static InputStreamReader newInputStreamReader4(InputStream stream)
    throws IOException {
        return Utils.newInputStreamReader(stream);
    }
}
