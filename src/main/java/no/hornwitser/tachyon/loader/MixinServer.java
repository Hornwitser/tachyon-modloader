package no.hornwitser.tachyon.loader;

import java.io.File;
import java.io.PrintStream;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
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

    // The first mixin
    @Redirect(method = "<init>", at = @At(
        value = "INVOKE",
        target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V",
        remap = false
    )) private void augmentVersion(PrintStream stream, String msg) {
        stream.println(msg + "-modded");
    }

    @Inject(method = "bf", remap = false, at = @At(
        value = "INVOKE",
        target = "Ltk/spgames/spaceteamserver/e;a(Ljava/util/HashMap;Ljava/util/HashMap;)Ljava/util/HashMap;",
        ordinal = 0,
        remap = false
    )) private static void loadModEvents(CallbackInfo ci) {
        logger.info("Loading mod events");
        aS parser = new aS();

        for (ModEntry mod : ModLoader.mods) {
            // logger.debug("Mod {}", mod.getPath());
            for (ModEntry.ModFile file : mod.getEventFiles()) {
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
    )) private static void loadModDialogues(CallbackInfo ci) {
        logger.info("Loading mod dialogues");
        aq parser = new aq();

        for (ModEntry mod : ModLoader.mods) {
            // logger.debug("Mod {}", mod.getPath());
            for (ModEntry.ModFile file : mod.getDialogueFiles()) {
                logger.debug("Parsing {}", file.getModPath());
                if (!file.isDirectory() && file.getName().endsWith(".xml")) {
                    parser.a(file.new PhantomFile());
                }
            }
        }
    }
}
