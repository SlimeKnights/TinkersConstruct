package tconstruct.library.armor;

import ic2.api.item.ICustomElectricItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ArmorCore extends Item implements ICustomElectricItem {

	public ArmorCore(int par1) {
		super(par1);
	}

	@Override
	public boolean canProvideEnergy(ItemStack itemStack) {
		return false;
	}

	@Override
	public int getChargedItemId(ItemStack itemStack) {
		return 0;
	}

	@Override
	public int getEmptyItemId(ItemStack itemStack) {
		return 0;
	}

	@Override
	public int getMaxCharge(ItemStack itemStack) {
		return 0;
	}

	@Override
	public int getTier(ItemStack itemStack) {
		return 0;
	}

	@Override
	public int getTransferLimit(ItemStack itemStack) {
		return 0;
	}

	@Override
	public int charge(ItemStack itemStack, int amount, int tier, boolean ignoreTransferLimit, boolean simulate) {
		return 0;
	}

	@Override
	public int discharge(ItemStack itemStack, int amount, int tier, boolean ignoreTransferLimit, boolean simulate) {
		return 0;
	}

	@Override
	public boolean canUse(ItemStack itemStack, int amount) {
		return false;
	}

	@Override
	public boolean canShowChargeToolTip(ItemStack itemStack) {
		return false;
	}

}
