package basashi.excblock.client.config;

import org.apache.commons.lang3.math.NumberUtils;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.ButtonEntry;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CycleIntegerEntry extends ButtonEntry {

	protected final int beforeValue;
	protected final int defaultValue;
	protected int currentValue;

	public CycleIntegerEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList,IConfigElement configElement){
		super(owningScreen, owningEntryList, configElement);
		this.beforeValue = NumberUtils.toInt(configElement.get().toString());
		this.defaultValue = NumberUtils.toInt(configElement.getDefault().toString());
		this.currentValue = beforeValue;
		this.btnValue.field_146124_l = enabled();
	}

	@Override
	public void updateValueButtonText() {
		// TODO 自動生成されたメソッド・スタブ
		btnValue.field_146126_j = I18n.func_135052_a(configElement.getLanguageKey() + "." + currentValue);
	}

	@Override
	public void valueButtonPressed(int slotIndex) {
		// TODO 自動生成されたメソッド・スタブ
		if (enabled()){
			if (++currentValue > NumberUtils.toInt(configElement.getMaxValue().toString())){
				currentValue = 0;
			}
			updateValueButtonText();
		}
	}

	@Override
	public boolean isDefault() {
		// TODO 自動生成されたメソッド・スタブ
		return currentValue == defaultValue;
	}

	@Override
	public void setToDefault() {
		// TODO 自動生成されたメソッド・スタブ
		if (enabled()){
			currentValue = defaultValue;
			updateValueButtonText();
		}
	}

	@Override
	public boolean isChanged() {
		// TODO 自動生成されたメソッド・スタブ
		return currentValue != beforeValue;
	}

	@Override
	public void undoChanges() {
		// TODO 自動生成されたメソッド・スタブ
		if (enabled()){
			currentValue = beforeValue;
			updateValueButtonText();
		}
	}

	@Override
	public boolean saveConfigElement() {
		// TODO 自動生成されたメソッド・スタブ
		if (enabled() && isChanged()){
			configElement.set(currentValue);
			return configElement.requiresMcRestart();
		}
		return false;
	}

	@Override
	public Integer getCurrentValue() {
		// TODO 自動生成されたメソッド・スタブ
		return currentValue;
	}

	@Override
	public Integer[] getCurrentValues() {
		// TODO 自動生成されたメソッド・スタブ
		return new Integer[] {getCurrentValue()};
	}

}
