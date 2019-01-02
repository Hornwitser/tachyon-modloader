package no.hornwitser.tachyon.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import no.hornwitser.tachyon.ModEntry;
import no.hornwitser.tachyon.ModLoader;

import moo.aq;
import moo.aS;
import tk.spgames.spaceteamserver.e;


@Mixin(e.class)
public abstract class MixinServer {
    private static Logger logger = LogManager.getLogger();

    @Shadow(remap = false)
    private static void b(File file) {}

    // The first mixin
    @Redirect(method = "<init>", at = @At(
        value = "INVOKE",
        target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V",
        remap = false
    ))
    private void augmentVersion(PrintStream stream, String msg) {
        stream.println(msg + "-modded");
    }

    @Inject(method = "bf", remap = false, at = @At(
        value = "INVOKE",
        target = "Ltk/spgames/spaceteamserver/e;a(Ljava/util/HashMap;Ljava/util/HashMap;)Ljava/util/HashMap;",
        ordinal = 0,
        remap = false
    ))
    private static void loadModEvents(CallbackInfo ci) {
        logger.info("Loading mod events");
        aS parser = new aS();

        for (ModEntry mod : ModLoader.mods) {
            // logger.debug("Mod {}", mod.getPath());
            for (
                ModEntry.ModFile file
                : mod.serverFile("WorldGen/Events").listFiles()
            ) {
                if (!file.isDirectory() && file.getName().endsWith(".xml")) {
                    logger.debug("Parsing {}", file.getModPath());
                    parser.a(file.new PhantomFile());
                }
            }
        }
    }

    @Inject(method = "be", remap = false, at = @At(
        value = "RETURN",
        remap = false
    ))
    private static void loadModDialogues(CallbackInfo ci) {
        logger.info("Loading mod dialogues");
        aq parser = new aq();

        for (ModEntry mod : ModLoader.mods) {
            // logger.debug("Mod {}", mod.getPath());
            for (
                ModEntry.ModFile file
                : mod.serverFile("WorldGen/Dialogues").listFiles()
            ) {
                if (!file.isDirectory() && file.getName().endsWith(".xml")) {
                    logger.debug("Parsing {}", file.getModPath());
                    parser.a(file.new PhantomFile());
                }
            }
        }
    }

    @Inject(method = "bg()V", remap = false, at = @At(
        value = "INVOKE",
        shift = At.Shift.AFTER,
        target = "Ltk/spgames/spaceteamserver/e;b(Ljava/io/File;)V",
        remap = false
    ))
    private static void loadGenShips(CallbackInfo ci) {
        logger.info("Loading mod GenShips");

        for (ModEntry mod : ModLoader.mods) {
            ModEntry.ModFile file = mod.serverFile("WorldGen/GenShips");
            b(file.new PhantomFile());
        }
    }


    private static File newFile(String path) {
        int pos = path.lastIndexOf(':');
        if (pos > 1) {
            String mod_file = path.substring(0, pos);
            String file_path = path.substring(pos+1);
            // logger.debug("Path {} {}", mod_file, file_path);

            ModEntry.ModFile file = ModLoader.mods.stream().filter(mod ->
                mod.mod_file.getPath().equals(mod_file)
            ).findFirst().get().file(file_path);
            return file.new PhantomFile();
        }
        return new File(path);
    }


    // Ugly as frig, but works
    private static ModEntry.ModFile next_file;
    @Redirect(method = "a(Ljava/lang/String;)Ljava/util/ArrayList;", remap = false, at = @At(
        value = "NEW",
        target = "(Ljava/lang/String;)Ljava/io/FileInputStream;",
        remap = false
    ))
    private static FileInputStream newFileInputStream(String path)
    throws IOException {
        int pos = path.lastIndexOf(':');
        if (pos > 1) {
            String mod_file = path.substring(0, pos);
            String file_path = path.substring(pos+1);
            // logger.debug("Path {} {}", mod_file, file_path);

            next_file = ModLoader.mods.stream().filter(mod ->
                mod.mod_file.getPath().equals(mod_file)
            ).findFirst().get().file(file_path);

            return new FileInputStream(new File("icon.png"));

        }
        return new FileInputStream(path);
    }

    @Redirect(method = "a(Ljava/lang/String;)Ljava/util/ArrayList;", remap = false, at = @At(
        value = "NEW",
        target = "(Ljava/io/InputStream;)Ljava/io/InputStreamReader;",
        remap = false
    ))
    private static InputStreamReader newInputStreamReader(InputStream stream)
    throws IOException {
        if (next_file != null) {
            stream = next_file.openStream();
            next_file = null;
        }
        return new InputStreamReader(stream);
    }

    @Redirect(method = "a(Ljava/lang/String;)Ljava/util/ArrayList;", remap = false, at = @At(
        value = "NEW",
        target = "(Ljava/lang/String;)Ljava/io/File;",
        remap = false
    ))
    private static File newFile1(String path) { return newFile(path); }

    @Redirect(method = "b(Ljava/io/File;)V", remap = false, at = @At(
        value = "NEW",
        target = "(Ljava/lang/String;)Ljava/io/File;",
        remap = false
    ))
    private static File newFile2(String path) { return newFile(path); }
}
