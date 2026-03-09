package potioncontrol;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import potioncontrol.config.ConfigHandler;
import potioncontrol.config.EarlyConfigReader;
import potioncontrol.config.classdump.PotionClassWriter;
import potioncontrol.config.descriptions.DescriptionReader;
import potioncontrol.config.descriptions.EmptyPotionWriter;
import potioncontrol.config.descriptions.NamesReader;
import potioncontrol.config.potioninfojsons.PotionInfoConfigReader;
import potioncontrol.config.potioninfojsons.PotionInfoInferrerWriter;
import potioncontrol.config.potioninfojsons.PotionInfoWriter;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

@Mod(
        modid = PotionControl.MODID,
        version = PotionControl.VERSION,
        name = PotionControl.NAME,
        dependencies =
                "required-after:fermiumbooter@[1.3.2,);"+
                "before:potiondescriptions"
)
public class PotionControl {
    public static final String MODID = "potioncontrol";
    public static final String VERSION = "1.0.0";
    public static final String NAME = "PotionControl";
    public static final Logger LOGGER = LogManager.getLogger(PotionControl.NAME);
    public static Configuration CONFIG = null;
    public static boolean configNeedsSaving = false;
    public static boolean loadingComplete = false;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        try {
            Field field = ConfigManager.class.getDeclaredField("CONFIGS");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Configuration> map = (Map<String, Configuration>) field.get(null);
            CONFIG = map.get(new File(Loader.instance().getConfigDir(), MODID + ".cfg").getAbsolutePath());
        } catch (Exception e){
            CONFIG = new Configuration(new File(Loader.instance().getConfigDir(), MODID + ".cfg"));
        }

        PotionInfoConfigReader.preInit(); //read PotionInfo's from /potions
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        PotionClassWriter.postInit(); //write /tmp/potionclasses.dump for next startup (sad that this is after late mixin load. classgraph could fix that if i could get it to work. then i wouldn't even need a custom file)
        if(event.getSide() == Side.CLIENT) {
            DescriptionReader.readDescriptions();
            EmptyPotionWriter.write(DescriptionReader.PATH);
            NamesReader.init();
            EmptyPotionWriter.write(NamesReader.PATH);
        }

        PotionInfoConfigReader.applyManualOverrides(); //apply manual overrides for attribute modifiers

        //infer info from existing potion objects (can be used for testing and development, it creates the best fitting approximation of an potion). these are not loaded
        if(ConfigHandler.dev.printInferred) PotionInfoInferrerWriter.printInferred();
        if (ConfigHandler.debug.printLoaded) PotionInfoWriter.printLoaded();

        if(configNeedsSaving) ConfigManager.sync(MODID, Config.Type.INSTANCE);

        //this just as cache clear
        EarlyConfigReader.clearLines();

        loadingComplete = true;
    }
}