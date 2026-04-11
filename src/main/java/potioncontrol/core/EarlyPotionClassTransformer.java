package potioncontrol.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.util.Annotations;
import potioncontrol.config.EarlyConfigReader;
import potioncontrol.config.classdump.PotionClassReader;

import java.util.ArrayList;
import java.util.Set;

public class EarlyPotionClassTransformer implements IClassTransformer {
    private static final String mixinDesc = Type.getDescriptor(Mixin.class);

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        //This class modifies the @Mixin annotation of VanillaPotionMixin to include all early potions
        if(!name.equals("potioncontrol.mixin.vanilla.main.VanillaPotionMixin")) return basicClass;

        //Modify
        ClassNode classNode = new ClassNode(Opcodes.ASM5) {
            private AnnotationNode node;

            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                AnnotationVisitor node = super.visitAnnotation(desc, visible);
                if (desc.equals(mixinDesc)) {
                    this.node = (AnnotationNode) node;
                }
                return node;
            }

            @Override
            public void visitEnd() {
                Set<String> modifiedPotionClasses = PotionClassReader.getEarlyClasses(); // already doesnt contain early potions
                EarlyConfigReader.getClassBlacklistConfig().forEach(modifiedPotionClasses::remove);
                System.out.println("PotionControl modifying " + modifiedPotionClasses.size() + " early potion classes");
                Annotations.setValue(this.node, "targets", new ArrayList<>(modifiedPotionClasses));
            }
        };
        new ClassReader(basicClass).accept(classNode, 0);

        //Write back
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);
        return cw.toByteArray();
    }
}
