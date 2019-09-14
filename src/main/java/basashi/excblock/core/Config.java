package basashi.excblock.core;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeConfigSpec;

public class Config {

	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final General GENERAL = new General(BUILDER);
	public static final ForgeConfigSpec spec = BUILDER.build();
	private static int p_DefaultValue;
	private static boolean p_init = false;


	public static Boolean flattenType() {
		if (GENERAL.toFlat != null) {
			return GENERAL.toFlat.get();
		}
		return false;
	}

	public static boolean useLayerdCache() {
		return GENERAL.useLayerdCache.get();
	}

	public static class General{
		public final ForgeConfigSpec.ConfigValue<Boolean> toFlat;
		public final ForgeConfigSpec.ConfigValue<Boolean> useLayerdCache;
		public final ForgeConfigSpec.ConfigValue<String> layer;
		public final ForgeConfigSpec.ConfigValue<String> layerBlock;

		public General(ForgeConfigSpec.Builder builder){
			builder.push("General");
			toFlat = builder
					.comment("default:true")
					.define("toFlat",true);

			useLayerdCache = builder
					.comment("default: true")
					.define("useLayerdCache",true);
			layer = builder
					.comment("layer exchangeblock dimension.start.ent,dimension2.start2:end2 ...")
					.define("layer","0.1.5,-1.1.5,-1.120.126");
			layerBlock = builder
					.comment("exchangeBlock before1.after1,before2,after2")
					.define("layerBlock","minecraft:bedrock.minecraft:stone,minecraft:bedrock.minecraft:netherrack,minecraft:bedrock.minecraft:netherrack");
			builder.pop();
		}

		private List<LayerInfo> lyaerList = new ArrayList<LayerInfo>();
		private boolean initialize = false;

		public void init(){
			initialize = false;
			makeinfo();

		}

		public void makeinfo (){
			if (initialize){return;}
			lyaerList.clear();
			String[] number = layer.get().split(",");
			String[] blk = layerBlock.get().split(",");

			for (int i = 0; i < number.length; i++){
				if (blk.length > i){
					String[] sted = number[i].split(Pattern.quote("."));
					String[] bfaf = blk[i].split(Pattern.quote("."));
					if (sted.length == 3 || bfaf.length == 2){
						try{
							LayerInfo inf = new LayerInfo(new Integer(sted[0]).intValue(),
														  new Integer(sted[1]).intValue(),
														  new Integer(sted[2]).intValue(),
													      bfaf[0],
													      bfaf[1]);
							lyaerList.add(inf);
						}catch(Throwable e){
						}
					}
				}
			}
			initialize = true;
		}

		public List<LayerInfo> getInfoList(){
			return lyaerList;
		}
	}

	public static class LayerInfo{
		private int dimension;
		private int start;
		private int end;
		private Block before;
		private Block after;

		public LayerInfo(int d, int s, int e, String b, String a){
			dimension = d;
			if (s < e){
				start = s;
				end = e;
			}else{
				start = e;
				end = s;
			}
			before = Registry.BLOCK.getValue(new ResourceLocation(b)).get();
			after = Registry.BLOCK.getValue(new ResourceLocation(a)).get();
		}
		public int getStart(){return start;}
		public int getEnd(){return end;}
		public Block getBefore(){return before;}
		public Block getAfter(){return after;}


		public boolean ExecExchange(final IChunk chunk)
		{
			World world = chunk.getWorldForge().getWorld();
			if (world.getDimension().getType().getId() != dimension)
			{
				return false;
			}

			if (world instanceof ServerWorld)
			{
				Minecraft.getInstance().execute(
					new Runnable()
					{
						@Override
						public void run()
						{
							ChunkPos cpos = chunk.getPos();
							for ( int stcnt =0; stcnt < chunk.getSections().length; stcnt++)
							{
								if ( (stcnt*16+15) < end || (stcnt*16) > start){continue;}
								ChunkSection section = chunk.getSections()[stcnt];
								if ( !section.isEmpty()){
									// yロケーションが範囲外の場合コンティニュー
									int location = section.getYLocation();
									if ( (location+15 < start) || (location > end)){continue;}

									int starty = ((start-location) < 0)?0:(start-location);
									int ency = ((end-location) < 16)?(end-location):16;


									for (int x = 0; x < 16; x++){
										for (int z = 0; z < 16; z++){
											for ( int y = starty; y < 16; ++y){
												if ( !((location + y) >= start && (location +y) <= end)){
													continue;
												}

												if (section.getBlockState(x, y, z).getBlock() == before){
													BlockPos bpos = new BlockPos(cpos.getXStart()+x, stcnt*16+y,cpos.getZStart()+z);
													section.setBlockState(x, y, z, after.getDefaultState());
													//world.setBlockState(bpos, after.getDefaultState());
												}
											}
										}
									}
								}
							}
							((net.minecraft.world.chunk.Chunk) chunk).markDirty();
						}
					});
			}
			return true;
		}
	}






}
