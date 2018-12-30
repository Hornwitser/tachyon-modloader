package no.hornwitser.tachyon;

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.transformer.MixinTransformer;

public class TachyonClassLoader extends URLClassLoader {
    private static Logger logger = LogManager.getLogger();
    
    public static MixinTransformer transformer;
    
    public TachyonClassLoader(
        URL[] urls, ClassLoader parent
    ) {
        super(urls, parent);
    }
    
    private byte[] readStream(InputStream stream) throws IOException {
        int chunk = 4096;
        int size = 1024*16;
        int offset = 0;
        byte[] buffer = new byte[size];
        
        while (true) {
            if (offset + chunk > size) {
                size *= 2;
                buffer = Arrays.copyOf(buffer, size);
            }
            
            int read = stream.read(buffer, offset, chunk);
            if (read < 0) {
                return Arrays.copyOf(buffer, offset);
                
            } else {
                offset += read;
            }
        }
    }
    
    public byte[] readResourceBytes(String resource) throws IOException {
        InputStream stream = getResourceAsStream(resource);
        try {
            return readStream(stream);
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                logger.info("Suppressing exception closing stream", e);
            }
        }
    }
    
    public Class<?> findClass(String name) throws ClassNotFoundException {
        // logger.debug("findClass '{}'", name);
        
        String resource = name.replace('.', '/').concat(".class");
        // logger.debug("Resource '{}'", resource);
        
        byte[] bytes;
        try {
            bytes = readResourceBytes(resource);
        } catch (IOException e) {
            logger.warn("Error reading {}", resource, e);
            throw new ClassNotFoundException();
        }
        
        bytes = transformer.transformClassBytes(name, name, bytes);
        return defineClass(name, bytes, 0, bytes.length);
        
        // return super.findClass(name);
    }
    
    /*
    public InputStream getResourceAsStream(String name) {
        logger.debug("getResourceAsStream '{}'", name);
        return super.getResourceAsStream(name);
    }
    
    // ClassLoader
    /*
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        logger.debug("loadClass '{}'", name);
        return super.loadClass(name);
    }
    
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        logger.debug("loadClass '{}' {}", name, resolve);
        return super.loadClass(name, resolve);
    }*/
}
