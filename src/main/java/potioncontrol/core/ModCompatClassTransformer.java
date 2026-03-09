package potioncontrol.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.*;

public class ModCompatClassTransformer implements IClassTransformer {

    public static final Set<String> targetMethods = new HashSet<>(Arrays.asList(
    ));

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        //removing abstract overriding methods
        if(PotionControlPlugin.potionClasses.contains(name)){
            ClassNode classNode = new ClassNode(Opcodes.ASM5);
            new ClassReader(basicClass).accept(classNode, 0);

            List<MethodNode> toRemove = new ArrayList<>();
            classNode.methods.stream()
                    .filter(method -> targetMethods.contains(method.name))
                    .filter(method -> (method.access & Opcodes.ACC_ABSTRACT) > 0)
                    .forEach(toRemove::add);

            if(!toRemove.isEmpty()) {
                classNode.methods.removeAll(toRemove);

                //Write back
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                classNode.accept(cw);
                return cw.toByteArray();
            }
        }
        return basicClass;
    }

}
