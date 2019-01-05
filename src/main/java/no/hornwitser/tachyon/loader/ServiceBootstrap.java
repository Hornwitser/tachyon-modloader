package no.hornwitser.tachyon.loader;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.util.Vector;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.service.IMixinServiceBootstrap;

import no.hornwitser.tachyon.TachyonClassLoader;

public class ServiceBootstrap implements IMixinServiceBootstrap {
    private static Logger logger = LogManager.getLogger();

    @Override
    public String getName() {
        return "TachyonBootstrap";
    }

    @Override
    public String getServiceClassName() {
        return "no.hornwitser.tachyon.mixin.Service";
    }

    @Override
    public void bootstrap() {
        logger.debug("Bootstrapping Tachyon Mixin Service");
        Vector<URL> sources = new Vector();
        try {
            sources.add(new File("TachyonServer.jar").toURI().toURL());

        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed URL");
        }

        Service.class_loader = new TachyonClassLoader(
            sources.toArray(new URL[0]),
            ClassLoader.getSystemClassLoader()
        );

        /* XXX ???
        classLoader.addClassLoaderExclusion("org.spongepowered.asm.service.");
        classLoader.addClassLoaderExclusion("org.spongepowered.asm.util.");
        classLoader.addClassLoaderExclusion("org.spongepowered.asm.lib.");
        classLoader.addClassLoaderExclusion("org.spongepowered.asm.mixin.");
        */
    }
}
