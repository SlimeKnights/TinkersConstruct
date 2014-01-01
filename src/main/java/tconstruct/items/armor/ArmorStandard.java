package tconstruct.items.armor;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

import net.minecraft.client.renderer.texture.IIconRegister;
import tconstruct.library.armor.*;

public class ArmorStandard extends ArmorCore
{

    public ArmorStandard(int baseProtection, EnumArmorPart armorPart)
    {
        super(baseProtection, armorPart);
    }

    @Override
    public void registerIcons (IIconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon("tinker:armor/wood_helmet");
    }

    @Override
    public String getArmorTexture (ItemStack stack, Entity entity, int slot, int layer)
    {
        return "tinker:textures/armor/wood_" + layer + ".png";
    }

}