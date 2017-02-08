package basashi.excblock.core;

import java.util.Random;

import com.google.common.base.Objects;

import basashi.excblock.core.log.ModLog;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ExchangeLayer {
	// 有効無効
	private boolean enable;
	// カテゴリ名
	private String categoryName;
	// ディメンジョン
	private int dimension;
	// 最小の高さ
	private int minHeight;
	// 最大の高さ
	private int maxHeight;
	// 交換後ブロック
	private IBlockState[] afterBlock;
	// レート
	private int[] blockRate;
	// 交換対象ブロック
	private IBlockState[] targetBlock;


	// 乱数ジェネレータ
	private Random rnd = new Random();
	// 乱数
	private int bound=0;

	/**
	 * コンストラクタ
	 * @param categor コンフィグファイルのカテゴリ名y
	 */
	public ExchangeLayer(String category)
	{
		categoryName = category;
		initConfig();
	}

	/**
	 * 有効か無効かを返す
	 * @return
	 */
	public boolean isEnable(){
		return enable;
	}
	/**
	 * 初期化
	 */
	private void initConfig(){
		Configuration config = Config.config();
		// enable
		Property prop = config.get(categoryName, ModCommon.MOD_CONFIG_LAYER_ENABLE,false);
		prop.setComment("[default:" + prop.getDefault() + "]");
		this.enable = prop.getBoolean();
		ModLog.log().debug("enable value = "+this.enable);

		// ディメンジョン
		prop = config.get(categoryName, ModCommon.MOD_CONFIG_LAYER_DIMENSION,0);
		prop.setComment("[default:" + prop.getDefault() + "]");
		this.dimension = prop.getInt();
		ModLog.log().debug("dimension value = "+this.dimension);

		// y方向最小値
		prop = config.get(categoryName, ModCommon.MOD_CONFIG_LAYER_MIN,0);
		prop.setComment("[default:" + prop.getDefault() + "]");
		this.minHeight = prop.getInt();
		ModLog.log().debug("minHeight Value = " + this.minHeight);

		// y方向最大値
		prop = config.get(categoryName, ModCommon.MOD_CONFIG_LAYER_MAX,5);
		prop.setComment("[default:" + prop.getDefault() + "]");
		this.maxHeight = prop.getInt();
		ModLog.log().debug("maxHeight Value = " + this.maxHeight);

		// 対象ブロック
		prop = config.get(categoryName, ModCommon.MOD_CONFIG_LAYER_TARGET,new String[0]);
		prop.setComment("[default:" + prop.getDefault() + "]");
		String[] blocks = prop.getStringList();
		this.targetBlock = new IBlockState[blocks.length];
		ModLog.log().debug("targets num = " + targetBlock.length);
		for ( int i = 0; i < blocks.length; i++){
			ModLog.log().debug("brock name = " + blocks[i]);
			if (blocks[i].contains(":")){
				// メタデータ有り
				// ブロック名とメタ値を分割
				String[] divd = blocks[i].split(":");

				// ブロックを取得
				Block blk = Block.getBlockFromName(blocks[0]);
				int meta =0;
				try{
					// メタデータを数値化
				    meta = CommandBase.parseInt(divd[1], 0,16);
				}catch(Exception ex){
					// 数値にできない場合、デフォルトのブロックを使用する
					ModLog.log().error(divd[1] + "is no integer. get defaultstate.");
					meta = 0;
				}
				targetBlock[i] = blk.getStateFromMeta(meta);
			}else{
				targetBlock[i] = Block.getBlockFromName(blocks[i]).getDefaultState();
			}
			ModLog.log().debug("target brock[" +i+"] = " + targetBlock[i].getBlock().getRegistryName());
		}


		// 交換後ブロック
		prop = config.get(categoryName, ModCommon.MOD_CONFIG_LAYER_BLOCKS,new String[0]);
		prop.setComment("[default:" + prop.getDefault() + "]");
		blocks = prop.getStringList();
		this.afterBlock = new IBlockState[blocks.length];
		ModLog.log().debug("afterBlock num = " + afterBlock.length);
		for ( int i = 0; i < blocks.length; i++){
			ModLog.log().debug("brock name = " + blocks[i]);
			if (blocks[i].contains(":")){
				// メタデータ有り
				// ブロック名とメタ値を分割
				String[] divd = blocks[i].split(":");

				// ブロックを取得
				Block blk = Block.getBlockFromName(blocks[0]);
				int meta =0;
				try{
					// メタデータを数値化
				    meta = CommandBase.parseInt(divd[1], 0,16);
				}catch(Exception ex){
					// 数値にできない場合、デフォルトのブロックを使用する
					ModLog.log().error(divd[1] + "is no integer. get defaultstate.");
					meta = 0;
				}
				afterBlock[i] = blk.getStateFromMeta(meta);
			}else{
				afterBlock[i] = Block.getBlockFromName(blocks[i]).getDefaultState();
			}
			ModLog.log().debug("brock[" +i+"] = " + afterBlock[i].getBlock().getRegistryName());
		}

		// ブロック出現レート
		prop = config.get(categoryName, ModCommon.MOD_CONFIG_LAYER_BLOCKSRATES, new int[0]);
		prop.setComment("[default:" + prop.getDefault() + "]");
		int[] rate = prop.getIntList();

		blockRate = new int[blocks.length];
		ModLog.log().debug("blockRate num = " + blockRate.length);
		int addrate = 0;
		for ( int i =0; i< rate.length; i++){
			if ( i >= blockRate.length){break;}
			addrate += rate[i];
			blockRate[i] = addrate;
			ModLog.log().debug("maxHeight Value = " + rate[i] + "(" +addrate+")");
		}
		bound=addrate;
	}

	/**
	 * ハッシュコードを取得する
	 */
	@Override
	public int hashCode(){
		return Objects.hashCode(dimension,minHeight,maxHeight,targetBlock,afterBlock);
	}

	/**
	 * オブジェクトが等しいかどうか比較する
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ExchangeLayer){
			ExchangeLayer entry = (ExchangeLayer)obj;
			boolean ret = true;
			if (dimension == entry.dimension && minHeight == entry.minHeight && maxHeight == entry.maxHeight){
				if (targetBlock.length == entry.targetBlock.length && afterBlock.length == entry.afterBlock.length){
					for (int tcnt = 0; tcnt < targetBlock.length; tcnt++){
						if(targetBlock[tcnt]!=entry.targetBlock[tcnt]){ret = false;break;}
					}
					for (int acnt = 0; acnt < afterBlock.length && ret; acnt++){
						if(afterBlock[acnt]!=entry.afterBlock[acnt]){ret = false;break;}
					}
				}else{ret=false;}
			}else{ret = false;}
			return ret;
		}
		return false;
	}

	/**
	 * ブロック置換を実行する
	 * @param chunk チャンク
	 * @return 結果
	 */
	public boolean ExecExchange(final Chunk chunk)
	{
		World world = chunk.getWorld();
		if (world.provider.getDimension() != dimension)
		{
			return false;
		}

		if (world instanceof WorldServer)
		{
			((WorldServer)world).addScheduledTask(
				new Runnable()
				{
					@Override
					public void run()
					{
						for ( int stcnt =0; stcnt < chunk.getBlockStorageArray().length; stcnt++)
						{
							if ( (stcnt*16+15) < minHeight || (stcnt*16) > maxHeight){continue;}
							ExtendedBlockStorage strage = chunk.getBlockStorageArray()[stcnt];
							// ストレージが空の場合コンティニュー
							if ( strage == null){
								strage = new ExtendedBlockStorage(stcnt*16,true);
								chunk.getBlockStorageArray()[stcnt] = strage;
							}

							// yロケーションが範囲外の場合コンティニュー
							int location = strage.getYLocation();
							if ( (location+15) < minHeight || location > maxHeight){continue;}

							int starty = ((minHeight-location) < 0)?0:(minHeight-location);
							int ency = ((maxHeight-location) < 16)?(maxHeight-location):16;
							for (int x = 0; x < 16; x++){
								for (int z = 0; z < 16; z++){
									for ( int y = starty; y < 16; ++y){
										if ( !((location + y) >= minHeight && (location +y) <= maxHeight)){
											continue;
										}
										boolean exec = (targetBlock.length == 0);
										for (int tcnt = 0; tcnt < targetBlock.length; tcnt++){
											if (strage.get(x,y,z) == targetBlock[tcnt]){
												exec = true;
												break;
											}
										}
										if (!exec){
											continue;
										}
										int val = rnd.nextInt(bound);
										for ( int rcnt = 0; rcnt < blockRate.length; rcnt++){
											if (blockRate[rcnt] > val){
												try{
													strage.set(x,y,z,afterBlock[rcnt]);
												}catch(Exception ex){
												}
												break;
											}
										}
									}
								}
							}
						}
						chunk.setModified(true);
					}
				});
		}
		return true;
	}
}
