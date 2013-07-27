package tconstruct.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class FancyEntityItem extends EntityItem
{
    public FancyEntityItem(World par1World, double par2, double par4, double par6)
    {
        super(par1World, par2, par4, par6);
    }

    public FancyEntityItem(World par1World, double par2, double par4, double par6, ItemStack par8ItemStack)
    {
        this(par1World, par2, par4, par6);
        this.setEntityItemStack(par8ItemStack);
        this.lifespan = (par8ItemStack.getItem() == null ? 6000 : par8ItemStack.getItem().getEntityLifespan(par8ItemStack, par1World));
    }

    public FancyEntityItem(World par1World)
    {
        super(par1World);
    }
}
