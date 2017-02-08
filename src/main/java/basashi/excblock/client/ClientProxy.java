package basashi.excblock.client;

import basashi.excblock.client.config.CycleIntegerEntry;
import basashi.excblock.core.CommonProxy;
import basashi.excblock.core.Config;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy{
	@Override
	public void initializeConfigEntries()
	{
		Config.cycleInteger(CycleIntegerEntry.class);
	}
}