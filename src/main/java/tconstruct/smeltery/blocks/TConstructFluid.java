package tconstruct.smeltery.blocks;

import cpw.mods.fml.relauncher.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.*;
import tconstruct.library.TConstructRegistry;

public class TConstructFluid extends BlockFluidClassic
{
    String texture;
    boolean alpha;
    public IIcon stillIcon;
    public IIcon flowIcon;
    boolean overwriteFluidIcons = true;

    public TConstructFluid(Fluid fluid, Material material, String texture)
    {
        super(fluid, material);
        this.texture = texture;
        this.setCreativeTab(TConstructRegistry.blockTab);
    }

    public TConstructFluid(Fluid fluid, Material material, String texture, boolean alpha)
    {
        this(fluid, material, texture);
        this.alpha = alpha;
    }

    @Override
    public int getRenderBlockPass ()
    {
        return alpha ? 1 : 0;
    }

    @Override
    public void registerBlockIcons (IIconRegister iconRegister)
    {
        stillIcon = iconRegister.registerIcon("tinker:" + texture);
        flowIcon = iconRegister.registerIcon("tinker:" + texture + "_flow");

        if (overwriteFluidIcons)
            this.getFluid().setIcons(stillIcon, flowIcon);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (int side, int meta)
    {
        if (side == 0 || side == 1)
            return stillIcon;
        return flowIcon;
    }

    public void suppressOverwritingFluidIcons ()
    {
        overwriteFluidIcons = false;
    }

}