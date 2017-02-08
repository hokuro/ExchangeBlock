package basashi.excblock.core;

import java.util.Map;

import basashi.excblock.events.ExBlockEventHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = ModCommon.MOD_ID, guiFactory = ModCommon.MOD_PACKAGE+ModCommon.MOD_FACTRY)
public class ExchangeBlock {
	@SidedProxy(modId = ModCommon.MOD_ID, clientSide = ModCommon.MOD_PACKAGE+ModCommon.MOD_CLIENT_SIDE, serverSide=ModCommon.MOD_PACKAGE + ModCommon.MOD_SERVER_SIDE)
	public static CommonProxy proxy;

	@EventHandler
	public void construct(FMLConstructionEvent event){
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		proxy.initializeConfigEntries();
		Config.syncConfig();
	}

	@EventHandler
	public void init(FMLInitializationEvent event){
		MinecraftForge.EVENT_BUS.register(ExBlockEventHooks.instance());
	}

	@EventHandler
	public void loaded(FMLLoadCompleteEvent event)
	{
		Config.syncConfig();
	}

	@EventHandler
	public void onServerStarting(FMLServerStartingEvent event){
		ExBlockEventHooks.instance().initChunk();
	}

	@NetworkCheckHandler
	public boolean netCheckHandler(Map<String,String> mods, Side side){
		return true;
	}
}
