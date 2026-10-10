package potioncontrol.core;

import org.objectweb.asm.Type;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;

//Just a quick helper class to access (package-)private methods of CallbackInfo/-Returnable
public class CallbackInfoAccessor {
    private static final Method GET_CALLBACK_INFO_CLASS_NAME;
    private static final Method GET_RETURN_ACCESSOR;
    private static final Method GET_RETURN_DESCRIPTOR;

    static {
        try {
            GET_CALLBACK_INFO_CLASS_NAME = CallbackInfo.class.getDeclaredMethod("getCallInfoClassName", Type.class);
            GET_RETURN_ACCESSOR = CallbackInfoReturnable.class.getDeclaredMethod("getReturnAccessor", Type.class);
            GET_RETURN_DESCRIPTOR = CallbackInfoReturnable.class.getDeclaredMethod("getReturnDescriptor", Type.class);

            GET_CALLBACK_INFO_CLASS_NAME.setAccessible(true);
            GET_RETURN_ACCESSOR.setAccessible(true);
            GET_RETURN_DESCRIPTOR.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize MixinReflectionHelper", e);
        }
    }

    public static String getCallbackInfoClassName(Type returnType) {
        try {
            return (String) GET_CALLBACK_INFO_CLASS_NAME.invoke(null, returnType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to call CallbackInfo::getCallbackInfoClassName", e);
        }
    }

    public static String getReturnAccessor(Type returnType) {
        try {
            return (String) GET_RETURN_ACCESSOR.invoke(null, returnType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to call CallbackInfoReturnable::getReturnAccessor", e);
        }
    }

    public static String getReturnDescriptor(Type returnType) {
        try {
            return (String) GET_RETURN_DESCRIPTOR.invoke(null, returnType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to call CallbackInfoReturnable::getReturnDescriptor", e);
        }
    }
}