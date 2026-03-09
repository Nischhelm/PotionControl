package potioncontrol.util;

import net.minecraft.entity.ai.attributes.BaseAttribute;

import java.util.HashMap;
import java.util.Map;

public class BaseAttributeRegistry {
    public static final Map<String, BaseAttribute> registeredAttributes = new HashMap<>();

    public static void register(BaseAttribute baseAttribute) {
        registeredAttributes.put(baseAttribute.getName(),  baseAttribute);
    }

    public static BaseAttribute get(String attributeName) {
        return registeredAttributes.get(attributeName);
    }
}
