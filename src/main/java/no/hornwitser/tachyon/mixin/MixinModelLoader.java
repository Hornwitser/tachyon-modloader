package no.hornwitser.tachyon.mixin;

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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import no.hornwitser.tachyon.ModEntry;
import no.hornwitser.tachyon.ModLoader;

import tk.spgames.spaceteamserver.l;


@Mixin(l.class)
public abstract class MixinModelLoader {
    private static Logger logger = LogManager.getLogger();

    @Shadow(remap = false)
    private void a(File file) { }

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void loadModModels(CallbackInfo ci) {
        logger.info("Loading mod models");

        for (ModEntry mod : ModLoader.mods) {
            ModEntry.ModFile file = mod.serverFile("models");
            a(file.new PhantomFile());
        }
    }
}
