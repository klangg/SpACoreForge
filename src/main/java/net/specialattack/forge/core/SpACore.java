package net.specialattack.forge.core;

import java.io.File;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.specialattack.forge.core.client.MC;
import net.specialattack.forge.core.config.Config;
import net.specialattack.forge.core.config.ConfigCategory;
import net.specialattack.forge.core.config.ConfigValue;
import net.specialattack.forge.core.packet.PacketHandler;
import net.specialattack.forge.core.sync.SyncHandler;
import net.specialattack.forge.core.sync.packet.*;

@Mod(name = Objects.MOD_NAME, modid = Objects.MOD_ID, guiFactory = Objects.GUI_FACTORY)
public class SpACore extends SpACoreMod {

    @Mod.Instance(value = Objects.MOD_ID)
    public static SpACore instance;

    @SidedProxy(clientSide = Objects.CLIENT_PROXY, serverSide = Objects.SERVER_PROXY)
    public static CommonProxy proxy;

    public static File configFolder;
    // Config
    public static ConfigValue<Integer> refreshRate;
    public static ConfigValue<Integer> textureMapId;
    public static ConfigValue<Boolean> showReportBugs;
    public static ConfigValue<Boolean> replaceModOptions;

    public static PacketHandler<SyncPacket> syncPacketHandler;

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Objects.log = event.getModLog();

        SpACore.syncPacketHandler = new PacketHandler<SyncPacket>("SpACore", Packet1TrackingStatus.class, Packet2TrackingBegin.class, Packet3TrackingUpdate.class, Packet4InitiateClientTracking.class, Packet5TrackingEnd.class, Packet6SetInterval.class, Packet7TrackingStop.class);

        SpACore.configFolder = new File(event.getModConfigurationDirectory(), "SpACore");

        if (!SpACore.configFolder.exists()) {
            SpACore.configFolder.mkdir();
        }

        // Config
        ConfigCategory<?> category = new ConfigCategory(Configuration.CATEGORY_GENERAL, "spacore:config.general", null);
        SpACore.refreshRate = new ConfigValue<Integer>("refreshRate", "spacore:config.general.refreshRate", null, 5);
        SpACore.textureMapId = new ConfigValue<Integer>("textureMapId", "spacore:config.general.textureMapId", Side.CLIENT, 10);
        SpACore.textureMapId.setRequiresMcRestart(true);
        SpACore.showReportBugs = new ConfigValue<Boolean>("showReportBugs", "spacore:config.general.showReportBugs", Side.CLIENT, false);
        SpACore.replaceModOptions = new ConfigValue<Boolean>("replaceModOptions", "spacore:config.general.replaceModOptions", Side.CLIENT, true);
        this.config = new Config(event.getSuggestedConfigurationFile());
        this.config.addCategory(category);
        category.addValue(SpACore.refreshRate);
        category.addValue(SpACore.textureMapId);
        //category.addValue(SpACore.showReportBugs);
        category.addValue(SpACore.replaceModOptions);

        super.preInit(event);
    }

    @Override
    public ModInfo getModInfo() {
        return Objects.MOD_INFO;
    }

    @Override
    public SpACoreProxy getProxy() {
        return SpACore.proxy;
    }

    @Override
    public boolean configChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (MC.getWorld() != null) {
            SyncHandler.Client.sendUpdateInterval();
        }
        return true;
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        SyncHandler.Server.reset(); // Make sure it is reset
    }

    @Mod.EventHandler
    public void serverStopped(FMLServerStoppedEvent event) {
        SyncHandler.Server.reset();
    }

}
