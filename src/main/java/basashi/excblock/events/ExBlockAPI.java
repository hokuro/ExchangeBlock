package basashi.excblock.events;

import basashi.excblock.core.Config;
import basashi.excblock.core.ExchangeLayer;
import basashi.excblock.core.log.ModLog;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.config.Configuration;

public class ExBlockAPI {
	// インスタンス
	public static final ExBlockAPI instance = new ExBlockAPI();

	private ExBlockAPI(){}

	/**
	 * コンフィグ情報を取得する
	 */
	public Configuration getConfig(){
		return Config.config();
	}

	/**
	 * ブロック交換条件を登録する
	 */
	public void registExBlock(String category){
		ModLog.log().debug("call registerFlatten");
		ExBlockEventHooks.instance().AddExchangeLayer(new ExchangeLayer(category));
		ModLog.log().debug("finish registerFlatten");
	}

	/**
	 * ブロック交換を実行する
	 */
	public boolean ExecExchange(Chunk chunk){
		boolean flag = false;
		for (ExchangeLayer  entry : ExBlockEventHooks.instance().exchangeSet()){
			if (entry.ExecExchange(chunk) && !flag){
				flag = true;
			}
		}
		return flag;
	}

	public void unregistExBlock(){
		ModLog.log().debug("call unregisterFlatten All");
		ExBlockEventHooks.instance().initLayerSet();
		ModLog.log().debug("finish unregisterFlatten All");
	}
}
