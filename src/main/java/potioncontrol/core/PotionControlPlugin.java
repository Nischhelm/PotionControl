package potioncontrol.core;

import fermiumbooter.FermiumRegistryAPI;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@IFMLLoadingPlugin.MCVersion("1.12.2")
public class PotionControlPlugin implements IFMLLoadingPlugin {
	public static final Set<String> actuallyEarlyPotions = new HashSet<>();
	public static final Set<String> potionClasses = new HashSet<>();

	public PotionControlPlugin() {
		MixinBootstrap.init();

		FermiumRegistryAPI.enqueueMixin(false, "mixins.potioncontrol.vanilla.json", () -> {
			graphClasses(); //this is a good position in the loading process, so we do it here, right during MC init while early jsons are enqueued
			return true;
		});
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[]{
				ModCompatClassTransformer.class.getName(),
				EarlyPotionClassTransformer.class.getName(),
				LatePotionClassTransformer.class.getName()
		};
	}
	
	@Override public String getModContainerClass() {return null;}
	@Override public String getSetupClass() {return null;}
	@Override public void injectData(Map<String, Object> data) { }
	@Override public String getAccessTransformerClass() {return null;}

	public static void graphClasses(){
		try (ScanResult scanResult = new ClassGraph()
				//.verbose()               // Log to stderr
				.enableClassInfo()
				.enableAnnotationInfo()
				.rejectPackages("java.*")
				.rejectPackages("potioncontrol.*")
				.rejectPackages("org.spongepowered.*")
				.rejectPackages("net.minecraftforge.*")
				.rejectPackages("com.google.common.*")
				.rejectPackages("com.mojang.*")
				.rejectPackages("org.objectweb.asm.*")
				.rejectPackages("io.github.classgraph.classpath.*")
				.rejectPackages("nonapi.io.github.classgraph.classpath.*")
				.rejectPackages("com.llamalad7.mixinextras.*")
				.scan()
		) {
			for (ClassInfo routeClassInfo : scanResult.getSubclasses("net.minecraft.potion.Potion")) {
				potionClasses.add(routeClassInfo.getName());
			}
			for (ClassInfo routeClassInfo : scanResult.getSubclasses("uz")) { //Obfuscated class name of net.minecraft.potion.Potion
				potionClasses.add(routeClassInfo.getName());
			}
			for (ClassInfo routeClassInfo : scanResult.getClassesWithAnnotation("org.spongepowered.asm.mixin.Mixin")) {
				potionClasses.remove(routeClassInfo.getName());
			}
		}
	}
}