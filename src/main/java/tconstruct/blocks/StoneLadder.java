package tconstruct.blocks;

import net.minecraft.block.BlockLadder;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class StoneLadder extends BlockLadder
{

    // Override ladder material
    public final Material blockMaterial = Material.field_151576_e;

    private IIcon icon;

    // Use the normally protected constructor
    public StoneLadder()
    {
        super();
        this.func_149663_c("decoration.stoneladder");
        this.func_149647_a(CreativeTabs.tabDecorations);
    }

    @Override
    public void func_149651_a (IIconRegister register)
    {
        icon = register.registerIcon("tinker:ladder_stone");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon func_149691_a (int meta, int side)
    {
        return icon;
    }

}
