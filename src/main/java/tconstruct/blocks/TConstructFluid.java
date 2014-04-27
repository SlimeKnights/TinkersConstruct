package tconstruct.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import tconstruct.library.TConstructRegistry;

public class TConstructFluid extends BlockFluidClassic
{
    String texture;
    boolean alpha;
    public Icon stillIcon;
    public Icon flowIcon;
    int blockColor = 0xFFFFFF;

    public TConstructFluid(int id, Fluid fluid, Material material, String texture, int color)
    {
        super(id, fluid, material);
        this.texture = texture;
        this.setCreativeTab(TConstructRegistry.blockTab);
        this.blockColor = color;
    }

    public TConstructFluid(int id, Fluid fluid, Material material, String texture, boolean alpha, int color)
    {
        this(id, fluid, material, texture, color);
        this.alpha = alpha;
    }

    @Override
    public int getRenderBlockPass ()
    {
        return alpha ? 1 : 0;
    }

    @Override
    public void registerIcons (IconRegister iconRegister)
    {
        stillIcon = iconRegister.registerIcon("tinker:" + texture + "_still");
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
    @SideOnly(Side.CLIENT)
    public int getBlockColor ()
    {
        return blockColor;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderColor (int par1)
    {
        return blockColor;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int colorMultiplier (IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return blockColor;
    }
}
