package xaeroplus.neo;

import com.github.benmanes.caffeine.cache.RemovalCause;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import xaeroplus.Globals;
import xaeroplus.XaeroPlus;
import xaeroplus.module.ModuleManager;
import xaeroplus.settings.XaeroPlusSettingRegistry;
import xaeroplus.settings.XaeroPlusSettingsReflectionHax;

import java.util.List;

@Mod(value = "xaeroplus")
public class XaeroPlusNeo {
    public static final IEventBus FORGE_EVENT_BUS = NeoForge.EVENT_BUS;
    public XaeroPlusNeo(IEventBus modEventBus) {
        if (FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(this::onInitialize);
            modEventBus.addListener(this::onRegisterKeyMappingsEvent);
//			FORGE_EVENT_BUS.register(modEventBus);
            RemovalCause explicit = RemovalCause.EXPLICIT; // force class load to stop forge shitting itself at runtime??
        }
    }

    public void onInitialize(FMLClientSetupEvent event) {
        // this is called after RegisterKeyMappingsEvent for some reason
    }

    public void onRegisterKeyMappingsEvent(final RegisterKeyMappingsEvent event) {
        if (XaeroPlus.initialized.compareAndSet(false, true)) {
            ModuleManager.init();
            boolean a = Globals.FOLLOW; // force static instances to init
            XaeroPlusSettingRegistry.fastMapSetting.getValue(); // force static instances to init
            List<KeyMapping> keybinds = XaeroPlusSettingsReflectionHax.keybindsSupplier.get();
            keybinds.forEach(event::register);
        }
    }
}
