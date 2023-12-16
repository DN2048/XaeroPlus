package xaeroplus;

import com.github.benmanes.caffeine.cache.RemovalCause;
import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.lenni0451.lambdaevents.LambdaManager;
import net.lenni0451.lambdaevents.generator.LambdaMetaFactoryGenerator;
import net.minecraft.client.option.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xaeroplus.module.ModuleManager;
import xaeroplus.settings.XaeroPlusSettingRegistry;
import xaeroplus.settings.XaeroPlusSettingsReflectionHax;

import java.util.List;

import static net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext.get;

@Mod(value = "xaeroplus")
public class XaeroPlus {
	public static final Logger LOGGER = LoggerFactory.getLogger("XaeroPlus");
    public static final LambdaManager EVENT_BUS = LambdaManager.basic(new LambdaMetaFactoryGenerator());
	public static final IEventBus FORGE_EVENT_BUS = MinecraftForge.EVENT_BUS;

	public XaeroPlus() {
		IEventBus modEventBus = get().getModEventBus();
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> {
			return () -> {
				MixinExtrasBootstrap.init();
				LOGGER.info("Initializing XaeroPlus");
				modEventBus.addListener(this::onInitialize);
				modEventBus.addListener(this::onRegisterKeyMappingsEvent);
				FORGE_EVENT_BUS.register(modEventBus);
				RemovalCause explicit = RemovalCause.EXPLICIT; // force class load to stop forge shitting itself at runtime??
			};
		});
	}

	public void onInitialize(FMLClientSetupEvent event) {
		// this is called after RegisterKeyMappingsEvent for some reason
	}

	public void onRegisterKeyMappingsEvent(final RegisterKeyMappingsEvent event) {
		ModuleManager.init();
		boolean a = Globals.FOLLOW; // force static instances to init
		XaeroPlusSettingRegistry.fastMapSetting.getValue(); // force static instances to init
		List<KeyBinding> keybinds = XaeroPlusSettingsReflectionHax.keybindsSupplier.get();
		keybinds.forEach(event::register);
	}
}
