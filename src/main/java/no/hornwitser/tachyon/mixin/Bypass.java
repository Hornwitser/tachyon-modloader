package no.hornwitser.tachyon.mixin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.transformer.MixinTransformer;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.MixinEnvironment.Phase;

public class Bypass {
    private static Logger logger = LogManager.getLogger();

    public static MixinTransformer newInstance() {
        try {
            Class<MixinTransformer> mixin_class = MixinTransformer.class;
            Constructor<MixinTransformer> constructor =
                mixin_class.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            logger.fatal("Error creating MixinTransformer", e);
            return null;
        }
    }
    
    public static void gotoPhase(Phase phase) {
        try {
            Class<MixinEnvironment> env_class = MixinEnvironment.class;
            Method method = env_class.getDeclaredMethod(
                "gotoPhase", Phase.class
            );
            method.setAccessible(true);
            method.invoke(null, phase);
        } catch (ReflectiveOperationException e) {
            logger.fatal("Invoking gotoPhase", e);
        }
    }
}