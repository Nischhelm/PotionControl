package potioncontrol.core;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.AbstractClassTransformer;
import meldexun.asmutil2.NonLoadingClassWriter;
import meldexun.asmutil2.reader.ClassUtil;
import meldexun.betterconfig.asm.BetterConfigClassTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import potioncontrol.config.ConfigHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

public class PotionClassTransformer extends AbstractClassTransformer implements IClassTransformer {

    @SuppressWarnings("deprecation")
    private static final ClassUtil REMAPPING_CLASS_UTIL = ReflectionHelper.getPrivateValue(BetterConfigClassTransformer.class, null, "REMAPPING_CLASS_UTIL");

    @Override
    protected byte[] transformOrNull(String obfName, String name, byte[] basicClass) {
        if (basicClass == null) return null;

        // check if class is or extends Potion
        try {
            if (REMAPPING_CLASS_UTIL.findInClassHierarchy(name.replace('.', '/'), "net/minecraft/potion/Potion"::equals) == null)
                return null;
        } catch (MissingResourceException e) {
            return null; // Exception happens for Optional.Interface for example
        }

        if (ConfigHandler.debug.disabledClasses.contains(name)) return null;

        ClassNode classNode = new ClassNode();
        new ClassReader(basicClass).accept(classNode, 0);

        // Transform all Potion methods with hooks
        for (MethodNode method : classNode.methods) {
            if ((method.access & Opcodes.ACC_ABSTRACT) != 0) continue;
            if ((method.access & Opcodes.ACC_STATIC) != 0) continue;
            if ((method.access & Opcodes.ACC_SYNTHETIC) != 0) continue;

            switch (method.name) {
                // Basic properties
                case "isBadEffect": case "func_76398_f": if (method.desc.equals("()Z"))
                    injectAtHeadAndMaybeCancel(method, "isBadEffect", Type.BOOLEAN_TYPE, Opcodes.IRETURN); break;
                case "isBeneficial": case "func_188408_i": if (method.desc.equals("()Z"))
                    injectAtHeadAndMaybeCancel(method, "isBeneficial", Type.BOOLEAN_TYPE, Opcodes.IRETURN); break;
                case "isInstant": case "func_76403_b": if (method.desc.equals("()Z"))
                    injectAtHeadAndMaybeCancel(method, "isInstant", Type.BOOLEAN_TYPE, Opcodes.IRETURN); break;
                case "performEffect": case "func_76394_a": if (method.desc.equals("(Lnet/minecraft/entity/EntityLivingBase;I)V"))
                    injectAtHeadAndMaybeCancel(method, "performEffect_isRepeating", Type.VOID_TYPE, Opcodes.RETURN); break;
                case "isReady": case "func_76397_a": if (method.desc.equals("(II)Z"))
                    injectAtHeadAndMaybeCancel(method, "isReady", Type.BOOLEAN_TYPE, Opcodes.IRETURN); break;
                case "getLiquidColor": case "func_76401_j": if (method.desc.equals("()I"))
                    injectAtHeadAndMaybeCancel(method, "getLiquidColor", Type.INT_TYPE, Opcodes.IRETURN); break;
                case "getCurativeItems": if (method.desc.equals("()Ljava/util/List;"))
                    modifyReturnValue(method, "getCurativeItems", Type.getType(List.class), Opcodes.ARETURN); break;
            }
        }

        // Write the transformed class
        ClassWriter classWriter = new NonLoadingClassWriter(ClassWriter.COMPUTE_FRAMES, REMAPPING_CLASS_UTIL);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    /**
     * Replaces all instances of return returnVal; with
     * return PotionHooks.hookMethod(this, returnVal);
     */
    private void modifyReturnValue(MethodNode method, String hookMethod, Type returnType, int returnOpcode) {
        // Find all instances of returnOpcode
        List<InsnNode> returnNodes = new ArrayList<>();
        ASMUtil.forEach(method.instructions, InsnNode.class,
                insnNode -> insnNode.getOpcode() == returnOpcode,
                insnNode -> returnNodes.add(insnNode)
        );

        // Replace them with return PotionHooks.hookMethod(this, returnVal);
        for (InsnNode insnNode : returnNodes) {
            InsnList injectedInstructions = new InsnList();
            injectedInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            injectedInstructions.add(new InsnNode(Opcodes.SWAP));
            injectedInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "potioncontrol/core/PotionHooks", hookMethod, "(Lnet/minecraft/potion/Potion;" + returnType + ")" + returnType, false));
            injectedInstructions.add(new InsnNode(returnOpcode));

            ASMUtil.replace(method, insnNode, injectedInstructions);
        }
    }

    /**
     * Injects the following lines at the top of the targeted method:
     * CallbackInfoReturnable cir = new CallbackInfoReturnable("potioncontrol_asm", true);
     * hookMethod(this, params..., cir);
     * if(ci.isCancelled()) return ci.getReturnValue();
     */
    private static void injectAtHeadAndMaybeCancel(MethodNode method, String hookMethod, Type returnType, int returnOpcode) {
        String callbackInfoClassName = CallbackInfoAccessor.getCallbackInfoClassName(returnType);
        int ciLVTSlot = getFirstOpenLVTSlot(method);

        InsnList injectedInstructions = new InsnList();

        // CallbackInfo ci = new CallbackInfo("potioncontrol_asm", true); --- or CallbackInfoReturnable
        injectedInstructions.add(new TypeInsnNode(Opcodes.NEW, callbackInfoClassName));
        injectedInstructions.add(new InsnNode(Opcodes.DUP));
        injectedInstructions.add(new LdcInsnNode("potioncontrol_asm"));
        injectedInstructions.add(new InsnNode(Opcodes.ICONST_1));
        injectedInstructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, callbackInfoClassName, "<init>", "(Ljava/lang/String;Z)V", false));
        injectedInstructions.add(new VarInsnNode(Opcodes.ASTORE, ciLVTSlot));

        // hookMethod(this, params, ci);
        loadParameters(injectedInstructions, method);
        injectedInstructions.add(new VarInsnNode(Opcodes.ALOAD, ciLVTSlot)); // callback info
        injectedInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "potioncontrol/core/PotionHooks", hookMethod, buildHookMethodDescriptor(method.desc, callbackInfoClassName), false));

        // if(ci.isCancelled() == 0) [jump to labelToOriginal]
        injectedInstructions.add(new VarInsnNode(Opcodes.ALOAD, ciLVTSlot));
        injectedInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, callbackInfoClassName, "isCancelled", "()Z", false));
        LabelNode labelToOriginal = new LabelNode();
        injectedInstructions.add(new JumpInsnNode(Opcodes.IFEQ, labelToOriginal));

        // else ...
        if (returnType.equals(Type.VOID_TYPE)) {
            // return;
            injectedInstructions.add(new InsnNode(Opcodes.RETURN));
        } else {
            // return ci.getReturnValue(); --- or any of the primitive variants
            injectedInstructions.add(new VarInsnNode(Opcodes.ALOAD, ciLVTSlot));
            String returnAccessor = CallbackInfoAccessor.getReturnAccessor(returnType);
            String returnDescriptor = CallbackInfoAccessor.getReturnDescriptor(returnType);
            injectedInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, callbackInfoClassName, returnAccessor, returnDescriptor, false));
            if (returnType.getSort() >= Type.ARRAY) injectedInstructions.add(new TypeInsnNode(Opcodes.CHECKCAST, returnType.getInternalName())); //objects need to check type
            injectedInstructions.add(new InsnNode(returnOpcode));
        }

        // original code
        injectedInstructions.add(labelToOriginal);

        method.instructions.insert(injectedInstructions);
    }

    private static void loadParameters(InsnList insnList, MethodNode method) {
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));

        int slot = 1;

        for (Type type : Type.getArgumentTypes(method.desc)) {
            switch (type.getSort()) {
                case Type.BOOLEAN: case Type.BYTE: case Type.CHAR: case Type.SHORT: case Type.INT:
                    insnList.add(new VarInsnNode(Opcodes.ILOAD, slot)); break;
                case Type.FLOAT: insnList.add(new VarInsnNode(Opcodes.FLOAD, slot)); break;
                case Type.LONG: insnList.add(new VarInsnNode(Opcodes.LLOAD, slot)); break;
                case Type.DOUBLE: insnList.add(new VarInsnNode(Opcodes.DLOAD, slot)); break;
                case Type.ARRAY: case Type.OBJECT:
                    insnList.add(new VarInsnNode(Opcodes.ALOAD, slot)); break;
            }
            slot += type.getSize();
        }
    }

    // Transform target method descriptor (X)Y
    // to hook method descriptor (Lnet/minecraft/potion/Potion;X;L..callbackInfo..;)V
    private static String buildHookMethodDescriptor(String originalDesc, String callbackInfoClass) {
        String targetMethodParams = originalDesc.substring(1, originalDesc.indexOf(')'));
        return "(Lnet/minecraft/potion/Potion;" + targetMethodParams + "L" + callbackInfoClass + ";)V";
    }

    // Calculate first available local variable slot for CallbackInfo
    private static int getFirstOpenLVTSlot(MethodNode method) {
        Type[] argTypes = Type.getArgumentTypes(method.desc);
        int slot = 1; // Start after 'this'
        for (Type type : argTypes)
            slot += type.getSize();
        return slot;
    }
}