package tconstruct.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import tconstruct.library.TConstructRegistry;

public class TConstructFluid extends BlockFluidClassic
{
    String texture;
    boolean alpha;
    public IIcon stillIcon;
    public IIcon flowIcon;

    public TConstructFluid(int id, Fluid fluid, Material material, String texture)
    {
        super(id, fluid, material);
        this.texture = texture;
        //TODO setCreativeTab()
        this.func_149647_a(TConstructRegistry.blockTab);
    }

    public TConstructFluid(int id, Fluid fluid, Material material, String texture, boolean alpha)
    {
        this(id, fluid, material, texture);
        this.alpha = alpha;
    }

    @Override
    public int func_149701_w ()
    {
        return alpha ? 1 : 0;
    }
    //TODO registerIcons
    @Override
    public void func_149651_a (IIconRegister iconRegister)
    {
        stillIcon = iconRegister.registerIcon("tinker:" + texture);
        flowIcon = iconRegister.registerIcon("tinker:" + texture + "_flow");
    }

    //TODO getIcon()
    @Override
    public IIcon func_149691_a (int side, int meta)
    {
        if (side == 0 || side == 1)
            return stillIcon;
        return flowIcon;
    }
}
