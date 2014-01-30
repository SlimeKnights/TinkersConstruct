package tconstruct.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;
import tconstruct.common.TContent;
import tconstruct.common.TRepo;
import tconstruct.library.TConstructRegistry;

public class LiquidMetalFinite extends BlockFluidFinite
{
    String texture;
    public IIcon stillIcon;
    public IIcon flowIcon;

    public LiquidMetalFinite(Fluid fluid, String texture)
    {
        super(fluid, TRepo.liquidMetal);
        this.texture = texture;
        this.func_149647_a(TConstructRegistry.blockTab);
    }

    @Override
    public int func_149701_w ()
    {
        return 0;
    }

    @Override
    public void func_149651_a (IIconRegister iconRegister)
    {
        stillIcon = iconRegister.registerIcon("tinker:" + texture);
        flowIcon = iconRegister.registerIcon("tinker:" + texture + "_flow");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon func_149691_a (int side, int meta)
    {
        if (side == 0 || side == 1)
            return stillIcon;
        return flowIcon;
    }

    @Override
    public void func_149670_a (World par1World, int x, int y, int z, Entity entity)
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
