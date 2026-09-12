package potioncontrol;

import meldexun.betterconfig.api.BetterConfigManager;
import net.minecraft.init.PotionTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import potioncontrol.config.ConfigHandler;
import potioncontrol.config.classdump.PotionClassWriter;
import potioncontrol.config.descriptions.DescriptionReader;
import potioncontrol.config.descriptions.EmptyPotionWriter;
import potioncontrol.config.descriptions.NamesReader;
import potioncontrol.config.potioninfojsons.PotionInfoConfigReader;
import potioncontrol.config.potioninfojsons.PotionInfoInferrerWriter;
import potioncontrol.config.potioninfojsons.PotionInfoWriter;
import potioncontrol.config.potiontypeinfojsons.PotionTypeInfoConfigReader;
import potioncontrol.config.potiontypeinfojsons.PotionTypeInfoInferrerWriter;
import potioncontrol.config.potiontypeinfojsons.PotionTypeInfoWriter;
import potioncontrol.handlers.PotionAddedHandler;
import potioncontrol.network.PacketHandler;
import potioncontrol.util.PotionInfo;
import potioncontrol.util.PotionTypeInfo;

@Mod(
        modid = Tags.MODID,
        version = Tags.VERSION,
        name = Tags.NAME,
        dependencies =
                "required-after:fermiumbooter@[1.5.0,);"+
                "required:betterconfig;"+//@[1.2.0,);"+
                "before:potiondescriptions"
)
public class PotionControl {
    //TODO: blacklisted creature attributes (undead/artrh)
    public static final Logger LOGGER = LogManager.getLogger(Tags.NAME);
    public static boolean configNeedsSaving = false;
    public static boolean loadingComplete = false;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if(ConfigHandler.mixinToggles.syncPotionsDistance >= 0) PacketHandler.preInit();

        if(ConfigHandler.mixinToggles.modifyMaxAmpDura) MinecraftForge.EVENT_BUS.register(PotionAddedHandler.class);

        PotionInfoConfigReader.preInit(); //read PotionInfo's from /potions
        PotionTypeInfoConfigReader.preInit(); //read PotionTypeInfo's from /potiontypes
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
        PotionTypeInfoConfigReader.applyManualOverrides(); //apply manual overrides (nothing yet)

        //infer info from existing potion objects (can be used for testing and development, it creates the best fitting approximation of an potion). these are not loaded
        if(ConfigHandler.dev.printInferredPotions) PotionInfoInferrerWriter.printInferred();
        if (ConfigHandler.debug.printLoaded) PotionInfoWriter.printLoaded();
        if(ConfigHandler.dev.printInferredTypes) PotionTypeInfoInferrerWriter.printInferred();
        if (ConfigHandler.debug.printLoaded) PotionTypeInfoWriter.printLoaded();

        if(configNeedsSaving) BetterConfigManager.sync(Tags.MODID);

        loadingComplete = true;
    }

    @Mod.EventBusSubscriber
    public static class EventHandler{
        @SubscribeEvent
        public static void onRegisterPotionTypes(RegistryEvent.Register<PotionType> event) {
            if(!ConfigHandler.dev.shouldCreatePotionTypes) return;
            PotionTypeInfo.getAll().stream()
                    .filter(info -> PotionType.getPotionTypeForName(info.id) == PotionTypes.EMPTY)
                    .forEach(info -> {
                        LOGGER.info("Registering Custom PotionType {}", info.id);
                        event.getRegistry().register(info.create());
                    });
        }
        @SubscribeEvent
        public static void onRegisterPotions(RegistryEvent.Register<Potion> event) {
            if(!ConfigHandler.dev.shouldCreatePotions) return;
            PotionInfo.getAll().stream()
                    .filter(info -> Potion.getPotionFromResourceLocation(info.id) == null)
                    .forEach(info -> {
                        LOGGER.info("Registering Custom Potion {}", info.id);
                        event.getRegistry().register(info.create());
                    });
        }
    }
}