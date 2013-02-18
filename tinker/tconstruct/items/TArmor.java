package tinker.tconstruct.items;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IArmorModelProvider;
import net.minecraftforge.common.IArmorTextureProvider;

public class TArmor extends ItemArmor
	implements IArmorTextureProvider, IArmorModelProvider
{
    private ModelBiped modelArmorChestplate;
	public TArmor(int par1, EnumArmorMaterial par2EnumArmorMaterial, int par3, int par4)
	{
		super(par1, par2EnumArmorMaterial, par3, par4);
        this.modelArmorChestplate = new ModelBiped(4.0F);
	}

	@Override
	public String getArmorTextureFile (ItemStack itemstack)
	{
		return "/tinkertextures/armor/wood_1.png";
	}

	@Override
	public ModelBiped provideArmorModel (int armorSlot)
	{
		/*modelArmorChestplate.bipedHead.showModel = armorSlot == 0;
        modelArmorChestplate.bipedHeadwear.showModel = armorSlot == 0;
        modelArmorChestplate.bipedBody.showModel = armorSlot == 1 || armorSlot == 2;
        modelArmorChestplate.bipedRightArm.showModel = armorSlot == 1;
        modelArmorChestplate.bipedLeftArm.showModel = armorSlot == 1;
        modelArmorChestplate.bipedRightLeg.showModel = armorSlot == 2 || armorSlot == 3;
        modelArmorChestplate.bipedLeftLeg.showModel = armorSlot == 2 || armorSlot == 3;*/
		return modelArmorChestplate;
	}

}
