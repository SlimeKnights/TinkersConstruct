package tconstruct.items.armor;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ArmorBasic extends ItemArmor
{

    public String textureName;

    public ArmorBasic(ArmorMaterial par2EnumArmorMaterial, int par3, String textureName)
    {
        super(par2EnumArmorMaterial, 0, par3);
        this.textureName = textureName;
    }

    @Override
    public void registerIcons (IIconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon("tinker:armor/" + textureName + "_"
                + (this.armorType == 0 ? "helmet" : this.armorType == 1 ? "chestplate" : this.armorType == 2 ? "leggings" : this.armorType == 3 ? "boots" : "helmet"));
    }

    @Override
    public String getArmorTexture (ItemStack stack, Entity entity, int slot, String type)
    {
        return "tinker:textures/armor/" + textureName + "_" + type + ".png";
    }

}
