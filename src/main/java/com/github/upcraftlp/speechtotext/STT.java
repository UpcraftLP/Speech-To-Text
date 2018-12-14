package com.github.upcraftlp.speechtotext;

import com.github.upcraftlp.glasspane.api.util.logging.PrefixMessageFactory;
import com.github.upcraftlp.glasspane.util.ModUpdateHandler;
import edu.cmu.sphinx.api.*;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.*;

import java.io.IOException;

import static com.github.upcraftlp.speechtotext.STT.*;

@Mod(
        certificateFingerprint = FINGERPRINT_KEY,
        name = MODNAME,
        version = VERSION,
        acceptedMinecraftVersions = MCVERSIONS,
        clientSideOnly = true,
        modid = MODID,
        dependencies = DEPENDENCIES,
        updateJSON = UPDATE_JSON
)
public class STT {

    //Version
    public static final String MCVERSIONS = "[1.12.2,1.13)";
    public static final String VERSION = "@VERSION@";

    //Meta Information
    public static final String MODNAME = "Speech To Text";
    public static final String MODID = "stt";
    public static final String DEPENDENCIES = "required-after:glasspane;required-after:forge";
    public static final String UPDATE_JSON = "@UPDATE_JSON@";

    public static final String FINGERPRINT_KEY = "@FINGERPRINTKEY@";

    private static final Configuration sphinxConfig = new Configuration();
    public static volatile LiveSpeechRecognizer voiceRecognizer;

    private static final Logger logger = LogManager.getLogger(MODID, new PrefixMessageFactory(MODNAME));

    public static Logger getLogger() {
        return logger;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModUpdateHandler.registerMod(MODID);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ProgressManager.ProgressBar progressBar = ProgressManager.push("Loading Speech Recognition", 2);
        progressBar.step("Loading Configuration");
        logger.debug("loading speech recognizer configuration");
        sphinxConfig.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us"); //set path to acoustic model
        sphinxConfig.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict"); //set path to dictionary
        sphinxConfig.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin"); //set language model
        progressBar.step("Initializing Speech Recognizer");
        logger.debug("initializing speech recognizer");
        try {
            voiceRecognizer = new LiveSpeechRecognizer(sphinxConfig);
        }
        catch (IOException e) {
            logger.error("unable to initialize speech listener", e);
            FMLCommonHandler.instance().exitJava(1, false);
        }
        ProgressManager.pop(progressBar);
    }

}
