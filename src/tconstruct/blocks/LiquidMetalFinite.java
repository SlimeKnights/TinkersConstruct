package tconstruct.blocks;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;
import tconstruct.common.TContent;
import tconstruct.library.TConstructRegistry;

public class LiquidMetalFinite extends BlockFluidFinite
{
    String texture;
    public Icon stillIcon;
    public Icon flowIcon;

    public LiquidMetalFinite(int id, Fluid fluid, String texture)
    {
        super(id, fluid, TContent.liquidMetal);
        this.texture = texture;
        this.setCreativeTab(TConstructRegistry.blockTab);
    }
    @Override
    public int getRenderBlockPass(){
    	return 0;
    }
    @Override
    public void registerIcons (IconRegister iconRegister)
    {
        stillIcon = iconRegister.registerIcon("tinker:" + texture);
        flowIcon = iconRegister.registerIcon("tinker:" + texture + "_flow");
    }

    @Override
    public Icon getIcon (int side, int meta)
    {
        if (side == 0 || side == 1)
            return stillIcon;
        return flowIcon;
    }

    @Override
    public void onEntityCollidedWithBlock (World par1World, int x, int y, int z, Entity entity)
    {
        if (entity instanceof EntityLivingBase)
        {
            entity.motionX *= 0.4D;
            entity.motionZ *= 0.4D;
        }
        if (!(entity instanceof EntityItem) && !entity.isImmuneToFire())
        {
            entity.attackEntityFrom(DamageSource.lava, 4);
            entity.setFire(15);
        }
    }
}
