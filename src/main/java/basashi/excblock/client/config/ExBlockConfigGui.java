package basashi.excblock.client.config;

import java.util.List;

import com.google.common.collect.Lists;

import basashi.excblock.core.ModCommon;
import basashi.excblock.events.ExBlockAPI;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ExBlockConfigGui extends GuiConfig {
	public ExBlockConfigGui(GuiScreen parent){
		super(parent,getConfigElements(), ModCommon.MOD_ID,false,false,I18n.format(ModCommon.MOD_CONFIG_LANG + "title"));
	}

	private static List<IConfigElement> getConfigElements(){
		List<IConfigElement> list = Lists.newArrayList();
		Configuration config = ExBlockAPI.instance.getConfig();

		for (String category : config.getCategoryNames()){
			list.addAll(new ConfigElement(config.getCategory(category)).getChildElements());
		}
		return list;
	}
}
