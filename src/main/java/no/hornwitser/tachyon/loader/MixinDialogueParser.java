package no.hornwitser.tachyon.loader;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.SAXParser;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import no.hornwitser.tachyon.ModEntry;

import moo.aq;


@Mixin(aq.class)
public abstract class MixinDialogueParser {
    private static Logger logger = LogManager.getLogger();

    @Redirect(method = "a(Ljava/io/File;)V", remap = false, at = @At(
        value = "INVOKE",
        target = "Ljavax/xml/parsers/SAXParser;parse(Ljava/io/File;Lorg/xml/sax/helpers/DefaultHandler;)V",
        ordinal = 0,
        remap = false
    )) private void parseFile(
        SAXParser parser, File file, DefaultHandler dh
    ) throws IOException, SAXException {
        // Needed for PhantomFile
        // logger.debug("parse hook {}", file.getName());
        if (file instanceof ModEntry.ModFile.PhantomFile) {
            ModEntry.ModFile.PhantomFile phantom = (ModEntry.ModFile.PhantomFile)file;
            try {
                parser.parse(
                    phantom.openStream(), dh
                );
            } catch (SAXException | IOException e) {
                logger.error("Exception parsing {}", phantom.getModPath());
                throw e;
            }
        } else {
            parser.parse(file, dh);
        }
    }
}
