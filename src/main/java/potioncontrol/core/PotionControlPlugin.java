package potioncontrol.core;

import fermiumbooter.FermiumRegistryAPI;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.MixinEnvironment;
import potioncontrol.config.ConfigHandler;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(1001)
public class PotionControlPlugin implements IFMLLoadingPlugin {

	public PotionControlPlugin() {
		FermiumRegistryAPI.enqueueMixin(false, "mixins.potioncontrol.vanilla.accessors.json");
		FermiumRegistryAPI.enqueueMixin(false, "mixins.potioncontrol.vanilla.potionsync.json", () -> ConfigHandler.mixinToggles.syncPotionsDistance >= 0);
	}

	@Override
	public String[] getASMTransformerClass() {
		if(ConfigHandler.debug.enablePotionInjection)
			return new String[]{ PotionClassTransformer.class.getName() };
		return new String[0];
	}
	
	@Override public String getModContainerClass() {return null;}
	@Override public String getSetupClass() {return null;}
	@Override public void injectData(Map<String, Object> data) {
		if (Boolean.FALSE.equals(data.get("runtimeDeobfuscationEnabled"))) {
			MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
			CoreModManager.getReparseableCoremods().removeIf(s -> StringUtils.containsIgnoreCase(s, "fermiumbooter"));
		}
	}
	@Override public String getAccessTransformerClass() {return null;}
}