package no.hornwitser.tachyon;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import no.hornwitser.tachyon.mixin.Bypass;
import no.hornwitser.tachyon.mixin.Service;


public class ModLoader {
    private static Logger logger = LogManager.getLogger();
    public static Vector<ModEntry> mods = new Vector();

    static public Vector<ModEntry> gatherMods() {
        File mods_dir = new File("mods/");
        if (!mods_dir.isDirectory()) {
            if (!mods_dir.mkdir()) {
                logger.warn("Failed to create mods/ directory");
            } else {
                logger.info("Created mods/ directory");
            }
            return mods;
        }

        for (File entry : mods_dir.listFiles()) {
            try {
                mods.add(new ModEntry(entry));
            } catch (IOException e) {
                logger.error("Error loading {}", entry.getPath(), e);
            }
        }

        return mods;
    }

    static public void main(String[] args) {
        logger.info("Booting up Tachyon ModLoader");

        try {
            MixinBootstrap.init();
            logger.debug("Bootstraping Mixin");
            // MixinBootstrap.start();
            // MixinBootstrap.doInit(args);

            // MixinBootstrap.getPlatform().inject(); // XXX ??
            Mixins.addConfiguration("mixins.tachyon.json");

        } catch (Throwable th) {
            logger.fatal("Exception initializing Mixin", th);
            System.exit(1);
        }

        logger.info("Gathering mods");
        gatherMods();

        logger.info("Starting TachyonServer");
        Bypass.gotoPhase(MixinEnvironment.Phase.DEFAULT);
        TachyonClassLoader.transformer = Bypass.newInstance();

        Method main;
        try {
            Class<?> Main = Service.class_loader.loadClass(
                "tk.spgames.spaceteamserver.Main"
            );
            main = Main.getMethod("main", String[].class);

        } catch (
            NoSuchMethodException
            | ClassNotFoundException ex
        ) {
            logger.fatal("Unable to load main method", ex);
            System.exit(1);
            return; // Silly Java compiler
        }

        try {
            main.invoke(null, (Object)args);
        } catch (
            Throwable th
        ) {
            logger.fatal("Exception invoking main method", th);
            System.exit(1);
        }
    }
}
