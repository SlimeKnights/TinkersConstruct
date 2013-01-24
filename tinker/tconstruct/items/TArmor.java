package tinker.tconstruct.items;

import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IArmorTextureProvider;

public class TArmor extends ItemArmor
	implements IArmorTextureProvider
{

	public TArmor(int par1, EnumArmorMaterial par2EnumArmorMaterial, int par3, int par4)
	{
		super(par1, par2EnumArmorMaterial, par3, par4);
	}

	@Override
	public String getArmorTextureFile (ItemStack itemstack)
	{
		return "/tinkertextures/armor/wood_1.png";
	}

}
