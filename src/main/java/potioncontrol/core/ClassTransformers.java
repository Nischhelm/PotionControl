package potioncontrol.core;

import meldexun.asmutil2.ClassNodeClassTransformer;
import meldexun.asmutil2.ClassNodeTransformer;
import meldexun.asmutil2.HashMapClassNodeClassTransformer;
import meldexun.asmutil2.IClassTransformerRegistry;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.util.Annotations;
import potioncontrol.Tags;
import potioncontrol.config.ConfigHandler;
import potioncontrol.config.classdump.PotionClassReader;

import java.util.*;

public class ClassTransformers {
    private static final String mixinDesc = Type.getDescriptor(Mixin.class);

    private static boolean isLate = false;

    // Keep track of what is early and what is late
    public static class LogEarliness extends ClassNodeClassTransformer implements IClassTransformer {
        @Override
        protected List<ClassNodeTransformer> getClassNodeTransformers(String className) {
            if (!PotionControlPlugin.potionClasses.contains(className)) return Collections.emptyList();
            return Collections.singletonList(ClassNodeTransformer.create(0, classNode -> {
                if (isLate) return;
                if (PotionControlPlugin.potionClasses.contains(className))
                    PotionControlPlugin.actuallyEarlyPotions.add(className);
            }));
        }
    }

    // Modify @Mixin(targets = {...}) in VanillaPotionMixin (early) to include all early potions
    public static class EarlyPotions extends HashMapClassNodeClassTransformer implements IClassTransformer {
        @Override
        protected void registerTransformers(IClassTransformerRegistry registry) {
            registry.add("potioncontrol.mixin.vanilla.main.VanillaPotionMixin", ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS, classNode -> {
                classNode.invisibleAnnotations.stream().filter(anno -> anno.desc.equals(mixinDesc)).findFirst().ifPresent(anno -> {
                    Set<String> targetClasses = PotionClassReader.getEarlyClasses(); // read from file

                    ConfigHandler.debug.disabledClasses.forEach(targetClasses::remove);
                    Tags.LOGGER.info("PotionControl modifying {} early potion classes", targetClasses.size());
                    Annotations.setValue(anno, "targets", new ArrayList<>(targetClasses));
                });
            });
        }
    }

    // Modify @Mixin(targets = {...}) in modded.PotionMixin (late) to include all late potions
    public static class LatePotions extends HashMapClassNodeClassTransformer implements IClassTransformer {
        @Override
        protected void registerTransformers(IClassTransformerRegistry registry) {
            registry.add("potioncontrol.mixin.modded.PotionMixin", ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS, classNode -> {
                isLate = true; //we declare it being late when mixins loads this mixin class

                classNode.invisibleAnnotations.stream().filter(anno -> anno.desc.equals(mixinDesc)).findFirst().ifPresent(anno -> {
                    Set<String> targetClasses = new HashSet<>(PotionControlPlugin.potionClasses);
                    targetClasses.removeAll(PotionControlPlugin.actuallyEarlyPotions);

                    ConfigHandler.debug.disabledClasses.forEach(targetClasses::remove);
                    Tags.LOGGER.info("PotionControl modifying {} late potion classes", targetClasses.size());
                    Annotations.setValue(anno, "targets", new ArrayList<>(targetClasses));
                });
            });
        }
    }
}