/**
 *
 */
package basashi.excblock.events;

import java.util.Set;

import com.google.common.collect.Sets;

import basashi.excblock.core.Config;
import basashi.excblock.core.Config.LayerInfo;
import basashi.excblock.core.ExchangeLayer;
import basashi.excblock.core.ModCommon;
import basashi.excblock.core.log.ModLog;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

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

	@SubscribeEvent
	public void onServerStarting(FMLServerStartingEvent event){
		Config.GENERAL.init();
	}

	/**
	 * コンフィグ更新
	 * @param even イベント情報t
	 */
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event){
		// コンフィグに変化があった場合再読み込み
		if(event.getModID().equals(ModCommon.MOD_ID)){
			ModLog.log().debug("call onConfigChanged");
			Config.GENERAL.init();
		}
	}

	/**
	 * チャンクロードイベント
	 * @param event
	 */
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onChunkLoad(ChunkEvent.Load event){
		Config.GENERAL.makeinfo();
		IWorld world = event.getWorld();
		if (world.getWorld().isRemote || !Config.flattenType()){
			// ブロック交換なし
			return;
		}

		// チャンクの情報を取得
		IChunk chunk = event.getChunk();
		long chunkSeed = chunkXZ2Int(chunk.getPos().x, chunk.getPos().z) ^ world.getDimension().getType().getId();

		if( !Config.useLayerdCache() || !exchangeChunk.contains(chunkSeed)){
			for ( LayerInfo entry : Config.GENERAL.getInfoList()){
				entry.ExecExchange(chunk);
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


	public static long chunkXZ2Int(int x, int z)
	{
	   return (long)x & 4294967295L | ((long)z & 4294967295L) << 32;
	}

	/**
	 * 構造物生成イベント
	 * @param event
	 */
//	@SubscribeEvent(priority = EventPriority.HIGHEST)
//	public void onPostPopulateChunk(PopulateChunkEvent.Populate event){
//		World world = event.getWorld();
//
//		if(world.isRemote || Config.flattenType() != 1){
//			return;
//		}
//
//		Chunk chunk = world.getChunkFromChunkCoords(event.getChunkX(),event.getChunkZ());
//		if (chunk.isLoaded()){
//			for(ExchangeLayer entry : exchangeSet){
//				if (entry.isEnable()){
//					entry.ExecExchange(chunk);
//				}
//			}
//		}
//	}


	@SubscribeEvent
	public void tickEventServer(TickEvent.ServerTickEvent event) {

	}
}



