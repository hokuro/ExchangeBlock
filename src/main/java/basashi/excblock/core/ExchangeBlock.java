package basashi.excblock.core;

import basashi.excblock.events.ExBlockEventHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(ModCommon.MOD_ID)
public class ExchangeBlock {

	public ExchangeBlock(){
    	ModLoadingContext.get().
        registerConfig(
        		net.minecraftforge.fml.config.ModConfig.Type.COMMON,
        		Config.spec);

		MinecraftForge.EVENT_BUS.register(ExBlockEventHooks.instance());
	}


//
//	@SubscribeEvent
//	public boolean netCheckHandler(Map<String,String> mods, Side side){
//		return true;
//	}
}
