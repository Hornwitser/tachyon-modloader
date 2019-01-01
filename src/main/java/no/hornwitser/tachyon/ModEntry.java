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

        ModFile(String path) {
            this.path = path;
        }

        ModFile(ModFile parent, String path) {
            this.path = ensureDirPath(parent.path).concat(path);

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
                    ZipEntry entry = zip_file.getEntry(path);
                    if (entry == null) return false;
                    return entry.isDirectory();

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
                    if (end == -1) end = path.length() - 1;
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
            @Override public boolean exists() { throw ni(); }
            @Override public File getAbsoluteFile() { throw ni(); }
            @Override public String getAbsolutePath() { throw ni(); }
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
            @Override public boolean isDirectory() { throw ni(); }
            @Override public boolean isFile() { throw ni(); }
            @Override public boolean isHidden() { throw ni(); }
            @Override public long lastModified() { throw ni(); }
            @Override public long length() { throw ni(); }
            @Override public String[] list() { throw ni(); }
            @Override public String[] list(FilenameFilter f) { throw ni(); }
            @Override public File[] listFiles() { throw ni(); }
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

        } else if (path.getName().endsWith(".zip")) {
            zip_file = new ZipFile(path);
            zip_entries = zip_file.stream().collect(
                Collectors.toCollection(Vector::new)
            );
            type = Type.ZIP;

        } else {
            throw new IOException(
                "Urecognized type for mod ".concat(mod_file.getPath())
            );
        }

        scanMod();
    }

    private void scanMod() throws IOException {
        Vector<ModFile> level = new Vector();
        Vector<ModFile> nextLevel = new Vector();
        Vector<ModFile> candidates = new Vector();
        level.add(this.new ModFile(""));
        // logger.debug("Scanning Mod {}", mod_file.getPath());

        while (
            candidates.isEmpty() && !(level.isEmpty() && nextLevel.isEmpty())
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
                            nextLevel.add(entry);
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

            level = nextLevel;
            nextLevel = new Vector();
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

    public ModFile[] getEventFiles() {
        return this.new ModFile(server_dir, "WorldGen/Events").listFiles();
    }

    public ModFile[] getDialogueFiles() {
        return this.new ModFile(server_dir, "WorldGen/Dialogues").listFiles();
    }
}

