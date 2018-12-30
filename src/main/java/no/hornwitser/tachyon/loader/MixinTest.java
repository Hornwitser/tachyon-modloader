package no.hornwitser.tachyon.loader;

import java.io.PrintStream;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At;

import tk.spgames.spaceteamserver.e;

@Mixin(e.class)
public abstract class MixinTest {
    @Redirect(method = "<init>", at = @At(
        value = "INVOKE",
        target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V",
        remap = false
    ))
    private void augmentVersion(PrintStream stream, String msg) {
        stream.println(msg + "-modded");
    }
}
