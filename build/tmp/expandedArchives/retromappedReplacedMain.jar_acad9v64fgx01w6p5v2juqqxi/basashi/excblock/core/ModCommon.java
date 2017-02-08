package basashi.excblock.core;

public final class ModCommon {
	// デバッグモードかどうか
	public static boolean isDebug = false;

	// モッドID
    public static final String MOD_ID = "exchangeblock";
    // モッド名
    public static final String MOD_NAME = "ExchageBock";
    public static final String MOD_PACKAGE = "basashi.excblock";
    public static final String MOD_CLIENT_SIDE = ".client.ClientProxy";
    public static final String MOD_SERVER_SIDE = ".core.CommonProxy";
    public static final String MOD_FACTRY = ".client.config.ExBlockGuiFactory";
    // モッドバージョン
    public static final String MOD_VERSION = "1.0 Beta";
    // コンフィグファイル名
    public static final String MOD_CONFIG_FILE = "exchangeblock.cfg";
    // コンフィグ
    public static final String MOD_CONFIG_LANG = "exchangeblock.config.";

    // コンフィグカテゴリ debug
    public static final String MOD_CONFIG_CAT_DEBUG = "debug";
    public static final String MOD_CONFIG_DEBUG_DEBUG = "debug";

    // コンフィグ カテゴリー general
    public static final String MOD_CONFIG_CAT_GENELAL = "general";
    // コンフィグ flattenType
    public static final String MOD_CONFIG_GENELAL_FLATTENTYPE = "flattenType";
    public static final String MOD_CONFIG_GENELAL_USELAYERDCACHE = "useLayerdCache";
    public static final String MOD_CONFIG_GELELAL_LAYERS = "layers";

    // コンフィグ カテゴリー exchangeblock
    public static final String MOD_CONFIG_CAT_EXCBOLCK = "exchangeblock";
    public static final String MOD_CONFIG_EXCBLOCK_OVERWORLD = "overworld";
    public static final String MOD_CONFIG_EXCBLOCK_NETHERL = "netherLower";
    public static final String MOD_CONFIG_EXCBLOCK_NETHERR = "netherUpper";

    // コンフィグカテゴリー レイヤーカテゴリ
    public static final String MOD_CONFIG_LAYER_ENABLE = "enable";
    public static final String MOD_CONFIG_LAYER_DIMENSION = "dimension";
    public static final String MOD_CONFIG_LAYER_MIN = "min";
    public static final String MOD_CONFIG_LAYER_MAX = "max";
    public static final String MOD_CONFIG_LAYER_BLOCKS = "blocks";
    public static final String MOD_CONFIG_LAYER_BLOCKSRATES = "rate";
    public static final String MOD_CONFIG_LAYER_TARGET ="targets";
}
