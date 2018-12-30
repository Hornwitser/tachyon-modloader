package no.hornwitser.tachyon.mixin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Vector;
import java.net.URL;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.lib.ClassReader;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment.Phase;
import org.spongepowered.asm.service.IClassBytecodeProvider;
import org.spongepowered.asm.service.IClassProvider;
import org.spongepowered.asm.service.IMixinService;
import org.spongepowered.asm.service.ITransformer;
import org.spongepowered.asm.util.ReEntranceLock;

import no.hornwitser.tachyon.TachyonClassLoader;


public class Service implements IMixinService, IClassProvider, IClassBytecodeProvider {
    private static Logger logger = LogManager.getLogger();
    private final ReEntranceLock lock = new ReEntranceLock(1);
    public static TachyonClassLoader class_loader;

    // IMixinService
    @Override
    public String getName() {
        return "Tachyon";
    }

    @Override
    public boolean isValid() {
        logger.info("isValid");
        return true; // XXX: ???
    }

    @Override
    public void prepare() {
        logger.info("prepare");
    }

    @Override
    public Phase getInitialPhase() {
        return Phase.PREINIT; // XXX: ???
    }

    @Override
    public void init() {
        logger.info("init");
        // XXX throw new RuntimeException("NotImplemented");

    }

    @Override
    public void beginPhase() {
        logger.info("beginPhase");
        // XXX throw new RuntimeException("NotImplemented");

    }

    @Override
    public void checkEnv(Object bootSource) {
        logger.info("checkEnv");
        // XXX throw new RuntimeException("NotImplemented");
    }

    @Override
    public ReEntranceLock getReEntranceLock() {
        return this.lock;
    }

    @Override
    public IClassProvider getClassProvider() {
        return this;
    }

    @Override
    public IClassBytecodeProvider getBytecodeProvider() {
        logger.info("getBytecodeProvider");
        return this;
    }

    @Override
    public Collection<String> getPlatformAgents() {
        logger.info("getPlatformAgents");
        Vector agents = new Vector();
        // agents.add("no.hornwitser.tachyon.mixin.PlatformAgent");
        return agents;

    }

    @Override
    public InputStream getResourceAsStream(String name) {
        logger.info("getResourceAsStream {}", name);
        return class_loader.getResourceAsStream(name);
        // throw new RuntimeException("NotImplemented");

    }

    @Override
    public void registerInvalidClass(String className) {
        logger.info("registerInvalidClass");

    }

    @Override
    public boolean isClassLoaded(String className) {
        logger.info("isClassLoaded");
        return false;
        // throw new RuntimeException("NotImplemented");

    }

    @Override
    public String getClassRestrictions(String className) {
        logger.info("getClassRestrictions");
        return "";
    }

    @Override
    public Collection<ITransformer> getTransformers() {
        logger.info("getTransformers");
        throw new RuntimeException("NotImplemented");

    }

    @Override
    public String getSideName() {
        logger.info("getSideName");
        return "SERVER"; // TODO Detect side.
    }


    // IClassProvider
    @Override
    public URL[] getClassPath() {
        logger.info("getClassPath");
        for (URL u: class_loader.getURLs()) {
            logger.debug(u.toString());
        }
        return class_loader.getURLs();
        /*
        ClassLoader loader = ClassLoader.getSystemClassLoader();

        if (loader instanceof URLClassLoader) {
            return ((URLClassLoader)loader).getURLs();
        }

        return new URL[0];
        */
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        logger.info("findClass(String)");
        return class_loader.findClass(name);
    }

    @Override
    public Class<?> findClass(String name, boolean initialize)
        throws ClassNotFoundException
    {
        logger.info("findClass(String, boolean)");
        return Class.forName(name, initialize, class_loader);
    }

    @Override
    public Class<?> findAgentClass(String name, boolean initialize)
        throws ClassNotFoundException
    {
        logger.info("findAgentClass");
        throw new RuntimeException("NotImplemented");

    }

    // IClassBytecodeProvider
    @Override
    public byte[] getClassBytes(String name, String transformedName)
        throws IOException
    {
        logger.info("getClassBytes({}, {})", name, transformedName);
        String resource = name.replace('.', '/').concat(".class");
        return class_loader.readResourceBytes(resource);
    }

    @Override
    public byte[] getClassBytes(String name, boolean runTransformers)
        throws ClassNotFoundException, IOException
    {
        logger.info("getClassBytes({}, {})", name, runTransformers);
        byte[] bytes = getClassBytes(name, name);
        
        if (runTransformers) {
            // No transformers yet
        }
        
        if (bytes == null) {
            throw new ClassNotFoundException(name);
        }
        
        return bytes;
    }

    @Override
    public ClassNode getClassNode(String name)
        throws ClassNotFoundException, IOException
    {
        logger.info("getClassNode");
        ClassNode class_node = new ClassNode();
        ClassReader class_reader = new ClassReader(getClassBytes(name, true));
        class_reader.accept(class_node, 0);
        return class_node;
    }
}
