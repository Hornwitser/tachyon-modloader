package no.hornwitser.tachyon.mixin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import no.hornwitser.tachyon.ModEntry;
import no.hornwitser.tachyon.ModLoader;

public class Utils {

    // Ugly as frig, but works
    private static ModEntry.ModFile next_file;

    public static FileInputStream newFileInputStream(String path)
    throws IOException {
        int pos = path.lastIndexOf(':');
        if (pos > 1) {
            String mod_file = path.substring(0, pos);
            String file_path = path.substring(pos+1);
            // logger.debug("Path {} {}", mod_file, file_path);

            next_file = ModLoader.mods.stream().filter(mod ->
                mod.mod_file.getPath().equals(mod_file)
            ).findFirst().get().file(file_path);

            return new FileInputStream(new File("icon.png"));

        }
        return new FileInputStream(path);
    }

    public static InputStreamReader newInputStreamReader(InputStream stream)
    throws IOException {
        if (next_file != null) {
            stream = next_file.openStream();
            next_file = null;
        }
        return new InputStreamReader(stream);
    }
}
