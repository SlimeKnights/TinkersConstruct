package tconstruct.items.armor;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

import net.minecraft.client.renderer.texture.IconRegister;
import tconstruct.library.armor.*;

public class ArmorStandard extends ArmorCore
{

    public ArmorStandard(int par1, int baseProtection, EnumArmorPart armorPart)
    {
        super(par1, baseProtection, armorPart);
    }

    @Override
    public void registerIcons (IconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon("tinker:armor/wood_helmet");
    }

    @Override
    public String getArmorTexture (ItemStack stack, Entity entity, int slot, int layer)
    {
        return "tinker:textures/armor/wood_" + layer + ".png";
    }

}