package basashi.excblock.core;
import java.io.File;
import java.util.List;

import com.google.common.collect.Lists;

import basashi.excblock.core.log.ModLog;
import basashi.excblock.events.ExBlockAPI;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.GuiConfigEntries.IConfigEntry;
import net.minecraftforge.fml.common.Loader;

public class Config {
	// コンフィグ
	private static Configuration config = null;
	// フラットタイプ
	private static int flattenType;
	// レイヤーキャッシュ
	private static boolean useLayerdCache;
	// レイヤー配列
	private static String[] layers;
	// コンフィグ
	private static Class<? extends IConfigEntry> cycleInteger;

	/**
	 * flattenTypを取得する
	 * @return flattenType
	 */
	public static int flattenType(){
		return flattenType;
	}

	/**
	 * コンフィグ情報を返す
	 * @return コンフィグ情報
	 */
	public static Configuration config(){
		if(config == null){syncConfig();}
		return config;
	}

	/**
	 * cycleIntegerを取得
	 * @return
	 */
	public static Class<? extends IConfigEntry> cycleInteger(){
		return cycleInteger;
	}

	/**
	 * cycleIntegerを設定
	 * @param value
	 * @return
	 */
	public static Class<? extends IConfigEntry> cycleInteger(Class<? extends IConfigEntry> value){
		cycleInteger = value;
		return cycleInteger;
	}

	/**
	 * useLayerdCacheを取得する
	 * @return useLayerdChache
	 */
	public static boolean useLayerdCache(){
		return useLayerdCache;
	}

	// コンフィグファイル初期化
	public static void syncConfig()
	{
		if (config == null){
			// 読み込むコンフィグファイルをしてい
			File file = new File(Loader.instance().getConfigDir(),ModCommon.MOD_CONFIG_FILE);
			config = new Configuration(file);
			try{
				// コンフィグファイル読み込み
				config.load();
			}catch(Exception e){
				// ファイル読み込みに失敗した場合、bakファイルを作成
				File dest = new File(file.getParentFile(),file.getName()+"bak");
				if (dest.exists()){
					dest.delete();
				}
				file.renameTo(dest);
				ModLog.log().error("error reading config("+file.getName()+")");
			}
		}

		// コンフィグファイル generalカテゴリ読み込み
		String category = ModCommon.MOD_CONFIG_CAT_DEBUG;
		Property prop;

		// デバッグモード読み込み
		prop = config.get(category, ModCommon.MOD_CONFIG_DEBUG_DEBUG, false);
		ModCommon.isDebug = prop.getBoolean();
		if (!ModCommon.isDebug){
			config.removeCategory(config.getCategory(category));
			ModLog.log().info("isRelease");
		}
		ModLog.log().info("idDebug value = " + prop.getBoolean());
		ModLog.log().debug("debug value ="+ prop.getBoolean());


		// コンフィグファイル generalカテゴリ読み込み
		category = Configuration.CATEGORY_GENERAL;
		List<String> propOrder = Lists.newArrayList();

		// flattenType読み込み
		prop = config.get(category,  ModCommon.MOD_CONFIG_GENELAL_FLATTENTYPE,  0);
		prop.setComment(" [range:" +prop.getMinValue() + "~" + prop.getMaxValue() + ", default:" + prop.getDefault()+"]");
		propOrder.add(prop.getName());
		flattenType = MathHelper.clamp(prop.getInt(flattenType),Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		if(flattenType < 0 == flattenType > 1)
		{
			flattenType = 0;
			prop.set(flattenType);;
		}
		ModLog.log().debug("debug value="+prop.getInt());

		// useLayerdCache読み込み
		prop = config.get(category, ModCommon.MOD_CONFIG_GENELAL_USELAYERDCACHE,true);
		prop.setComment(" [default: " + prop.getDefault() + "]");
		propOrder.add(prop.getName());
		useLayerdCache = prop.getBoolean(useLayerdCache);
		ModLog.log().debug("useLayerdCache value=" + prop.getBoolean());

		// layers 読み込み
		prop = config.get(category, ModCommon.MOD_CONFIG_GELELAL_LAYERS, new String[]{"overworld","netherLower","netherUpper"});
		prop.setComment("[default:" + prop.getDefault() + "]");
		propOrder.add(prop.getName());
		layers = prop.getStringList();
		ExBlockAPI.instance.unregistExBlock();
		for (String layercat : prop.getStringList()){
			ModLog.log().debug("useLayerdCache value=" + layercat);
			ExBlockAPI.instance.registExBlock(layercat);
		}
		config.setCategoryPropertyOrder(category,  propOrder);

		if (config.hasChanged()){
			config.save();
		}
	}
}
