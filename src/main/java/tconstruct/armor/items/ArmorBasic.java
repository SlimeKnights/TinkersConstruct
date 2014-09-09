package tconstruct.armor.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.*;

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
        this.itemIcon = par1IconRegister.registerIcon("tinker:armor/" + textureName + "_" + (this.armorType == 0 ? "helmet" : this.armorType == 1 ? "chestplate" : this.armorType == 2 ? "leggings" : this.armorType == 3 ? "boots" : "helmet"));
    }

    @Override
    public String getArmorTexture (ItemStack stack, Entity entity, int slot, String type)
    {
        int suffix = this.armorType == 2 ? 2 : 1;
        return "tinker:textures/armor/" + textureName + "_" + suffix + ".png";
    }

}
