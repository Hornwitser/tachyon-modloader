package no.hornwitser.tachyon;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URI;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

// Represent a mod
public class ModEntry {
    private static Logger logger = LogManager.getLogger();

    public enum Type {
        DIR,
        ZIP,
        JAR,
    }

    private Type type;
    private ZipFile zip_file;
    private Vector<ZipEntry> zip_entries;
    private ModFile server_dir;

    public File mod_file;

    private static String ensureDirPath(String path) {
        if (!path.isEmpty() && path.charAt(path.length() - 1) != '/')
            return path.concat("/");
        return path;
    }


    // Represent a file in a mod
    public class ModFile {
        private String path;

        private void canonicalize() {
            path = path.replaceAll("//+", "/");
        }

        ModFile(String path) {
            this.path = path;
            canonicalize();
        }

        ModFile(ModFile parent, String path) {
            this.path = ensureDirPath(parent.path).concat(path);
            canonicalize();
        }

        public InputStream openStream() throws IOException {
            switch (type) {
                case DIR:
                    URL url;
                    try {
                        url = new File(mod_file, path).toURI().toURL();
                    } catch (MalformedURLException e) {
                        throw new RuntimeException("Impossible");
                    }
                    return url.openStream();

                case ZIP:
                    return zip_file.getInputStream(zip_file.getEntry(path));

                default:
                    throw new RuntimeException("Not Implemented");
            }
        }

        public boolean isDirectory() {
            switch (type) {
                case DIR:
                    return new File(mod_file, path).isDirectory();

                case ZIP:
                    // ZipEntry entry = zip_file.getEntry(path);
                    // if (entry == null) return false;
                    // return entry.isDirectory();
                    // Doesn't work with zip files that is missing dir entries
                    return listFiles().length > 0;


                default:
                    throw new RuntimeException("Not Implemented");
            }
        }

        public boolean exists() {
            switch (type) {
                case DIR:
                    return new File(mod_file, path).exists();

                case ZIP:
                        return zip_entries.stream().anyMatch(entry -> {
                            String name = entry.getName();
                            if (!path.endsWith("/") && name.endsWith("/")) {
                                return name.equals(path.concat("/"));
                            }
                            return name.equals(path);
                        });

                default:
                    throw new RuntimeException("Not Implemented");
            }
        }

        public String getName() {
            switch (type) {
                case DIR:
                    return new File(mod_file, path).getName();

                case ZIP:
                    int start = path.lastIndexOf('/', path.length() - 2) + 1;
                    int end = path.indexOf('/', start);
                    if (end == -1) end = path.length();
                    return path.substring(start, end);

                default:
                    throw new RuntimeException("Not Implemented");
            }
        }

        public String getModPath() {
            switch (type) {
                case DIR:
                    return new File(mod_file, path).getPath();

                case ZIP:
                    return mod_file + ":" + path;

                default:
                    throw new RuntimeException("Not Implemented");
            }
        }

        public ModFile[] listFiles() {
            switch (type) {
                case DIR:
                    {
                        String[] names = new File(mod_file, path).list();
                        if (names == null)
                            return new ModEntry.ModFile[0];
                        ModFile[] files = new ModEntry.ModFile[names.length];
                        for (int i=0; i < files.length; i++)
                            files[i] = new ModFile(path + "/" + names[i]);
                        return files;
                    }

                case ZIP:
                    {
                        return zip_entries.stream().filter(entry -> {
                            String name = entry.getName();
                            String path = ensureDirPath(this.path);
                            //logger.debug("{} {}", path, name);

                            if (!name.startsWith(path))
                                return false;

                            if (name.length() == path.length())
                                return false;

                            int index = name.indexOf('/', path.length());
                            return index == -1 || index == name.length() - 1;
                        }).map(
                            entry -> new ModFile(entry.getName())
                        ).toArray(ModFile[]::new);
                    }

                default:
                    throw new RuntimeException("Not Implemented");
            }
        }


        public class PhantomFile extends File{
            public PhantomFile() {
                super(mod_file, path);
            }

            public InputStream openStream() throws IOException {
                return ModFile.this.openStream();
            }

            public String getModPath() {
                return ModFile.this.getModPath();
            }

            @Override
            public String getName() {
                return ModFile.this.getName();
            }

            @Override
            public String getAbsolutePath() {
                return getModPath();
            }

            @Override
            public boolean exists() {
                // logger.debug("PhantomFile.exists {} {}", path, ModFile.this.exists());
                return ModFile.this.exists();
            }

            @Override
            public boolean isDirectory() {
                return ModFile.this.isDirectory();
            }

            @Override
            public File[] listFiles() {
                ModFile[] files = ModFile.this.listFiles();
                PhantomFile[] phantoms
                    = new ModEntry.ModFile.PhantomFile[files.length];

                for (int i=0; i < phantoms.length; i++)
                    phantoms[i] = files[i].new PhantomFile();
                return phantoms;
            }

            private RuntimeException ni() {
                return new RuntimeException("Not Implemneted");
            }

            // The class that says ni
            @Override public boolean canExecute() { throw ni(); }
            @Override public boolean canRead() { throw ni(); }
            @Override public boolean canWrite() { throw ni(); }
            @Override public int compareTo(File p) { throw ni(); }
            @Override public boolean createNewFile() { throw ni(); }
            @Override public boolean delete() { throw ni(); }
            @Override public void deleteOnExit() { throw ni(); }
            @Override public boolean equals(Object obj) { throw ni(); }
            @Override public File getAbsoluteFile() { throw ni(); }
            @Override public File getCanonicalFile() { throw ni(); }
            @Override public String getCanonicalPath() { throw ni(); }
            @Override public long getFreeSpace() { throw ni(); }
            @Override public String getParent() { throw ni(); }
            @Override public File getParentFile() { throw ni(); }
            @Override public String getPath() { throw ni(); }
            @Override public long getTotalSpace() { throw ni(); }
            @Override public long getUsableSpace() { throw ni(); }
            @Override public int hashCode() { throw ni(); }
            @Override public boolean isAbsolute() { throw ni(); }
            @Override public boolean isFile() { throw ni(); }
            @Override public boolean isHidden() { throw ni(); }
            @Override public long lastModified() { throw ni(); }
            @Override public long length() { throw ni(); }
            @Override public String[] list() { throw ni(); }
            @Override public String[] list(FilenameFilter f) { throw ni(); }
            @Override public File[] listFiles(FileFilter f) { throw ni(); }
            @Override public File[] listFiles(FilenameFilter f) { throw ni(); }
            @Override public boolean mkdir() { throw ni(); }
            @Override public boolean mkdirs() { throw ni(); }
            @Override public boolean renameTo(File d) { throw ni(); }
            @Override public boolean setExecutable(boolean e) { throw ni(); }
            @Override public boolean setExecutable(boolean e, boolean o) { throw ni(); }
            @Override public boolean setLastModified(long t) { throw ni(); }
            @Override public boolean setReadable(boolean r) { throw ni(); }
            @Override public boolean setReadable(boolean r, boolean o) { throw ni(); }
            @Override public boolean setReadOnly() { throw ni(); }
            @Override public boolean setWritable(boolean w) { throw ni(); }
            @Override public boolean setWritable(boolean w, boolean o) { throw ni(); }
            @Override public Path toPath() { throw ni(); }
            @Override public String toString() { throw ni(); }
            @Override public URI toURI() { throw ni(); }
            @SuppressWarnings( "deprecation" )
            @Override public URL toURL() { throw ni(); }
        }

        PhantomFile getPhantomFile() {
            return this.new PhantomFile();
        }
    }

    // Create entry based on path to the mod
    ModEntry(File path) throws IOException {
        mod_file = path;

        if (path.isDirectory()) {
            type = Type.DIR;
            scanMod();

        } else if (path.getName().endsWith(".zip")) {
            type = Type.ZIP;
            initZip(path);
            scanMod();

        } else if (path.getName().endsWith(".jar")) {
            type = Type.JAR;
            initZip(path);

        } else {
            throw new IOException(
                "Urecognized type for mod ".concat(mod_file.getPath())
            );
        }
    }

    private void initZip(File path) throws IOException {
        zip_file = new ZipFile(path);
        zip_entries = zip_file.stream().collect(
            Collectors.toCollection(Vector::new)
        );

        Set<String> zip_dirs = zip_entries.stream().filter(
            ZipEntry::isDirectory
        ).map(ZipEntry::getName).collect(
            Collectors.toCollection(HashSet::new)
        );

        Set<String> missing_dirs = zip_entries.stream().flatMap(entry -> {
            String name = entry.getName();
            Vector<String> nested = new Vector();
            int pos = -1;
            while ((pos = name.indexOf('/', pos+1)) != -1) {
                nested.add(name.substring(0, pos+1));
            }
            return nested.stream();
        }).collect(
            Collectors.toCollection(HashSet::new)
        );

        missing_dirs.removeAll(zip_dirs);
        for (String dir : missing_dirs) {
            logger.debug("Adding missing directory {}", dir);
            zip_entries.add(new ZipEntry(dir));
        }

    }

    private void scanMod() throws IOException {
        Vector<ModFile> level = new Vector();
        Vector<ModFile> next_level = new Vector();
        Vector<ModFile> candidates = new Vector();
        level.add(this.new ModFile(""));
        // logger.debug("Scanning Mod {}", mod_file.getPath());

        while (
            candidates.isEmpty() && !(level.isEmpty() && next_level.isEmpty())
        ) {
            for (ModFile dir : level) {
                for (ModFile entry : dir.listFiles()) {
                    // logger.info("Entry {}", entry.getModPath());
                    if (entry.isDirectory()) {
                        // logger.info("Entry name {}, {}", entry.getName(), entry.getName().equals("server"));
                        if (entry.getName().equals("server")) {
                            // logger.info("Adding as candidate");
                            candidates.add(entry);
                        } else {
                            next_level.add(entry);
                        }
                    }
                }
            }

            // logger.info("candidates size {}", candidates.size());
            if (candidates.size() == 1) {
                server_dir = candidates.firstElement();
                logger.debug("Found server dir {}", server_dir.getModPath());
                return;
            }

            level = next_level;
            next_level = new Vector();
        }

        String msg;
        if (candidates.isEmpty()) {
            msg = "Unable to find server dir";
        } else {
            msg = "Found multiple server dirs";
        }

        throw new IOException(msg);

    }

    public String getPath() {
        return mod_file.getPath();
    }

    public ModFile serverFile(String path) {
        return this.new ModFile(server_dir, path);
    }

    public ModFile file(String path) {
        return this.new ModFile(path);
    }
}

