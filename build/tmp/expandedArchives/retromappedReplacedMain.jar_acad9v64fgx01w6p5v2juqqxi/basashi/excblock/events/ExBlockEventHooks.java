/**
 *
 */
package basashi.excblock.events;

import java.util.Set;

import com.google.common.collect.Sets;

import basashi.excblock.core.Config;
import basashi.excblock.core.ExchangeLayer;
import basashi.excblock.core.ModCommon;
import basashi.excblock.core.log.ModLog;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author as
 *
 */
public class ExBlockEventHooks {
	public static final ExBlockEventHooks instance = new ExBlockEventHooks();
	private final Set<ExchangeLayer> exchangeSet = Sets.newLinkedHashSet();
	private final Set<Long> exchangeChunk = Sets.newHashSet();


	/**
	 * インスタンスを取得する
	 * @return インスタンス
	 */
	public static ExBlockEventHooks instance(){
		return instance;
	}

	/**
	 * 操作対象を返却する
	 * @return ブロック置換情報セット
	 */
	public Set<ExchangeLayer> exchangeSet(){
		return exchangeSet;
	}

	/**
	 * ブロック置換情報鵜をクリアする
	 */
	public void initLayerSet(){
		exchangeSet.clear();
	}


	/**
	 * チャンクのハッシュ情報をクリアする
	 */
	public void initChunk() {
		exchangeChunk.clear();
	}


	/**
	 * ブロック交換情報を登録する
	 * @param entry ブロック交換情報
	 */
	public void AddExchangeLayer(ExchangeLayer entry){
		exchangeSet.add(entry);
	}

	/**
	 * コンフィグ更新
	 * @param even イベント情報t
	 */
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event){
		// コンフィグに変化があった場合再読み込み
		if(event.getModID().equals(ModCommon.MOD_ID)){
			ModLog.log().debug("call onConfigChanged");
			Config.syncConfig();
		}
	}

	/**
	 * チャンクロードイベント
	 * @param event
	 */
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onChunkLoad(ChunkEvent.Load event){
		World world = event.getWorld();
		if (world.field_72995_K || Config.flattenType() != 0){
			// ブロック交換なし
			return;
		}

		// チャンクの情報を取得
		Chunk chunk = event.getChunk();
		long chunkSeed = ChunkCoordIntPair.func_77272_a(chunk.field_76635_g, chunk.field_76647_h) ^ world.field_73011_w.getDimension();

		if(chunk.func_177410_o() && (!Config.useLayerdCache() || !exchangeChunk.contains(chunkSeed))){
			for ( ExchangeLayer entry : exchangeSet){
				if (entry.isEnable()){
					// ブロック入れ替え
					entry.ExecExchange(chunk);
				}
			}

			if (Config.useLayerdCache()){
				// チャンクのシードを記録する
				exchangeChunk.add(chunkSeed);
			}
		}
	}

//	/**
//	 * 構造物生成イベント
//	 * @param event
//	 */
//	@SubscribeEvent(priority = EventPriority.HIGHEST)
//	public void onPrePopulateChunk(PopulateChunkEvent.Pre event){
//		World world = event.world;
//
//		if(world.isRemote || Config.flattenType() != 1){
//			return;
//		}
//
//		Chunk chunk = world.getChunkFromChunkCoords(event.chunkX,event.chunkZ);
//		if (chunk.isLoaded()){
//			for(ExchangeLayer entry : exchangeSet){
//				if (entry.isEnable()){
//					entry.ExecExchange(chunk);
//				}
//			}
//		}
//	}

	/**
	 * 構造物生成イベント
	 * @param event
	 */
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPostPopulateChunk(PopulateChunkEvent.Populate event){
		World world = event.getWorld();

		if(world.field_72995_K || Config.flattenType() != 1){
			return;
		}

		Chunk chunk = world.func_72964_e(event.getChunkX(),event.getChunkZ());
		if (chunk.func_177410_o()){
			for(ExchangeLayer entry : exchangeSet){
				if (entry.isEnable()){
					entry.ExecExchange(chunk);
				}
			}
		}
	}
}



