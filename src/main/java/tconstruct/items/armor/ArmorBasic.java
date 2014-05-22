package tconstruct.items.armor;

import tconstruct.library.TConstructRegistry;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.*;

public class ArmorBasic extends ItemArmor
{
    public String textureName;

    public ArmorBasic(int par1, EnumArmorMaterial par2EnumArmorMaterial, int par3, String textureName)
    {
        super(par1, par2EnumArmorMaterial, 0, par3);
        this.setCreativeTab(TConstructRegistry.equipableTab);
        this.textureName = textureName;
    }

    @Override
    public void registerIcons (IconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon("tinker:armor/" + textureName + "_"
                + (this.armorType == 0 ? "helmet" : this.armorType == 1 ? "chestplate" : this.armorType == 2 ? "leggings" : this.armorType == 3 ? "boots" : "helmet"));
    }

    @Override
    public String getArmorTexture (ItemStack stack, Entity entity, int slot, int layer)
    {
        return "tinker:textures/armor/" + textureName + "_" + layer + ".png";
    }

}
